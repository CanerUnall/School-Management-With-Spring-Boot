package com.project.contactmessage.controller;

import com.project.contactmessage.entity.ContactMessage;
import com.project.contactmessage.service.ContactMessageService;
import com.project.payload.response.business.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contactMessages")
@RequiredArgsConstructor
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    @PostMapping("/save")
    public ResponseMessage<ContactMessage> save() {
        return null;
    }

}
