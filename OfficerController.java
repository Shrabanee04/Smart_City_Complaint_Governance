package com.example.smartcitygovernance.controller;

import com.example.smartcitygovernance.dto.ComplaintResponse;
import com.example.smartcitygovernance.service.ComplaintService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/officer")
public class OfficerController {

    private final ComplaintService complaintService;

    public OfficerController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Officer view assigned complaints
    @GetMapping("/complaints")
    public List<ComplaintResponse> getAssignedComplaints(Authentication authentication) {

        String officerUsername = authentication.getName();

        return complaintService.getComplaintsByOfficer(officerUsername);
    }

    // Officer update complaint status
    @PutMapping("/complaints/{id}/status")
    public ComplaintResponse updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return complaintService.updateStatus(id, status);
    }
}