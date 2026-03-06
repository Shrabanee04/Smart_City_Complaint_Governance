package com.example.smartcitygovernance.serviceImpl;

import com.example.smartcitygovernance.dto.ComplaintRequest;
import com.example.smartcitygovernance.dto.ComplaintResponse;
import com.example.smartcitygovernance.model.Complaint;
import com.example.smartcitygovernance.model.User;
import com.example.smartcitygovernance.repository.ComplaintRepository;
import com.example.smartcitygovernance.repository.UserRepository;
import com.example.smartcitygovernance.service.ComplaintService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ComplaintResponse raiseComplaint(ComplaintRequest request, String username) {

        User citizen = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setLatitude(request.getLatitude());
        complaint.setLongitude(request.getLongitude());
        complaint.setCitizen(citizen);

        Complaint savedComplaint = complaintRepository.save(complaint);

        return mapToResponse(savedComplaint);
    }

    @Override
    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponse> getComplaintsByCitizen(String username) {

        User citizen = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return complaintRepository.findByCitizenId(citizen.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponse> getComplaintsByOfficer(String username) {

        User officer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return complaintRepository.findByAssignedOfficerId(officer.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ComplaintResponse updateStatus(Long complaintId, String status) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(status);

        Complaint updatedComplaint = complaintRepository.save(complaint);

        return mapToResponse(updatedComplaint);
    }


    // Convert Entity → DTO
    private ComplaintResponse mapToResponse(Complaint complaint) {

        ComplaintResponse response = new ComplaintResponse();
        response.setId(complaint.getId());
        response.setTitle(complaint.getTitle());
        response.setDescription(complaint.getDescription());
        response.setLatitude(complaint.getLatitude());
        response.setLongitude(complaint.getLongitude());
        response.setStatus(complaint.getStatus());
        response.setCreatedAt(complaint.getCreatedAt());

        if (complaint.getCitizen() != null)
            response.setCitizenName(complaint.getCitizen().getUsername());

        if (complaint.getAssignedOfficer() != null)
            response.setAssignedOfficerName(
                    complaint.getAssignedOfficer().getUsername());

        return response;
    }
}
