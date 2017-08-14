package org.chiwooplatform.samples.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;

public class ScanPageTemplate<T> {

    final Class<T> entityType;

    public ScanPageTemplate(Class<T> clazz) {
        super();
        this.entityType = clazz;
    }

    private int scanThroughResults(Iterator<T> paginatedScanListIterator,
            int resultsToScan) {
        int processed = 0;
        while (paginatedScanListIterator.hasNext() && processed < resultsToScan) {
            paginatedScanListIterator.next();
            processed++;
        }
        return processed;
    }

    private List<T> readPageOfResults(Iterator<T> paginatedScanListIterator,
            int pageSize) {
        int processed = 0;
        List<T> resultsPage = new ArrayList<>();
        while (paginatedScanListIterator.hasNext() && processed < pageSize) {
            resultsPage.add(paginatedScanListIterator.next());
            processed++;
        }
        return resultsPage;
    }

    public Page<T> scanPage(DynamoDBMapper mapper, DynamoDBScanExpression expression,
            Pageable pageable) {
        PaginatedScanList<T> paginatedScanList = mapper.scan(entityType, expression);

        Iterator<T> iterator = paginatedScanList.iterator();
        int processedCount = 0;
        if (pageable.getOffset() > 0) {
            processedCount = scanThroughResults(iterator, pageable.getOffset());
            if (processedCount < pageable.getOffset())
                return new PageImpl<T>(new ArrayList<T>());
        }

        List<T> results = readPageOfResults(iterator, pageable.getPageSize());
        int totalCount = mapper.count(entityType, expression);
        return new PageImpl<T>(results, pageable, totalCount);
    }

}
