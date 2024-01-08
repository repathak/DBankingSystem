package com.danskebank.dummy.service.impl;

import com.danskebank.dummy.dto.EmailDetails;

public interface EmailService {

        void sendEmailAlert(EmailDetails emailDetails);
        void sendEmailWithAttachment(EmailDetails emailDetails);

}
