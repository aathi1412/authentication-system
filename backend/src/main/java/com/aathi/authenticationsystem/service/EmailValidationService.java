package com.aathi.authenticationsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

@Service
public class EmailValidationService {

    private static final Logger log = LoggerFactory.getLogger(EmailValidationService.class);

    public boolean hasValidEmailDomain(String domain) {

        try {
            Lookup lookup = new Lookup(domain, Type.MX);
            Record[] records = lookup.run();

            return  records != null && records.length > 0;

        } catch (Exception e) {
            log.warn("MX lookup failed for domain: {}", domain, e);
            return false;
        }
    }
}
