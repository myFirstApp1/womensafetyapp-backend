/*
package com.womensafety.notificationservice.controller;

import com.womensafety.notificationservice.service.WhatsAppService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/whatsapp")
public class TestWhatsAppController {

    private final WhatsAppService whatsAppService;

    public TestWhatsAppController(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    @PostMapping("/send")
    public String sendTestMessage(@RequestParam String to, @RequestParam String message) {
        whatsAppService.sendWhatsAppMessage(to, message);
        return "Message sent to " + to;
    }
}
*/
