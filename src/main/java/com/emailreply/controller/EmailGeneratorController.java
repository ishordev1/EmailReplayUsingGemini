package com.emailreply.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailreply.entity.EmailRequest;
import com.emailreply.serviceimp.EmailGeneratorService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/email")
//@AllArgsConstructor // if this add then not need to write @autowired 
public class EmailGeneratorController {
	@Autowired
	private EmailGeneratorService emailGeneratorService;
	
	
	@PostMapping("/generate")
public ResponseEntity<String> generateEmail(@RequestBody EmailRequest req){
		String res=this.emailGeneratorService.generateEmailReply(req);
	return ResponseEntity.ok(res);
}
}
