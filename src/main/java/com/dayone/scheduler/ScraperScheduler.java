package com.dayone.scheduler;

import com.dayone.model.Company;
import com.dayone.model.ScrapedResult;
import com.dayone.model.constants.CacheKey;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.CompanyRepository;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.persist.entity.DividendRepository;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@EnableCaching
public class ScraperScheduler {
    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록 조회
        List<CompanyEntity> companies = companyRepository.findAll();

        // 회사마다 배당금 정보를 스크래핑
        for (var company : companies) {
            log.info("scheduler company ->" + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(new Company(company.getName(), company.getTicker()));

            //스크래핑한 배당금 중 DB에 없는 것을 저장
            scrapedResult.getDividendEntities().stream()
                    .map(e -> new DividendEntity(company.getId(), e))
                    .forEach(e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                            log.info("insert new dividend ->" + e.toString());
                        }
                    });
            // 연속적으로 스크래핑 사이트에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000);// 3초
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
