package com.hii.finalProject.stockMutation.controller;

import com.hii.finalProject.auth.helpers.Claims;
import com.hii.finalProject.response.Response;
import com.hii.finalProject.stockMutation.dto.StockMutationProcessDto;
import com.hii.finalProject.stockMutation.dto.StockMutationRequestDto;
import com.hii.finalProject.stockMutation.dto.StockMutationResponseDto;
import com.hii.finalProject.stockMutation.entity.StockMutation;
import com.hii.finalProject.stockMutation.entity.StockMutationStatus;
import com.hii.finalProject.stockMutation.service.StockMutationService;
import com.hii.finalProject.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stock-mutations")
public class StockMutationController {

    private final StockMutationService stockMutationService;

    public StockMutationController(StockMutationService stockMutationService) {
        this.stockMutationService = stockMutationService;
    }

    @PostMapping("/manual")
//    @PutMapping("/process")
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Response<StockMutationResponseDto>> createManualMutation(
            @RequestBody StockMutationRequestDto request) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");

        StockMutationResponseDto response = stockMutationService.createManualMutation(request, email);

        return Response.successfulResponse("Stock Mutation registered successfully", response);
    }

//    @PostMapping("/{mutationId}/process")
//    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
//    public ResponseEntity<Response<StockMutationResponseDto>> processMutation(
//            @PathVariable Long mutationId) {
//        var claims = Claims.getClaimsFromJwt();
//        var email = (String) claims.get("sub");
//
//        StockMutationResponseDto response = stockMutationService.processMutation(mutationId, email);
//
//        return Response.successfulResponse("Stock Mutation processed successfully", response);
//    }
    @PutMapping("/process")
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Response<StockMutationResponseDto>> processMutation(
            @RequestBody StockMutationProcessDto processDto) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");

        StockMutationResponseDto response = stockMutationService.processMutation(processDto, email);

        return Response.successfulResponse("Stock Mutation processed successfully", response);
    }
    @GetMapping("/{mutationId}")
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Response<StockMutationResponseDto>> getMutation(
            @PathVariable Long mutationId) {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");
        StockMutationResponseDto response = stockMutationService.getMutationById(mutationId);

        return Response.successfulResponse("Stock Mutation retrieved successfully", response);
    }
    @GetMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Response<List<StockMutationResponseDto>>> getUserMutations() {
        var claims = Claims.getClaimsFromJwt();
        var email = (String) claims.get("sub");

        List<StockMutationResponseDto> response = stockMutationService.getMutationByUser(email);

        return Response.successfulResponse("User Stock Mutations retrieved successfully", response);
    }
//    @GetMapping("/user")
//    @PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
//    public ResponseEntity<Response<List<StockMutationResponseDto>>> getAllStock() {
//        var claims = Claims.getClaimsFromJwt();
//        var email = (String) claims.get("sub");
//
//        List<StockMutationResponseDto> response = stockMutationService.getAllStock();
//
//        return Response.successfulResponse("User Stock Mutations retrieved successfully", response);
//    }
@GetMapping
@PreAuthorize("hasAuthority('SCOPE_SUPER') or hasAuthority('SCOPE_ADMIN')")
public ResponseEntity<Response<Page<StockMutationResponseDto>>> getAllStockMutations(
        @RequestParam(required = false) Long originWarehouseId,
        @RequestParam(required = false) Long destinationWarehouseId,
        @RequestParam(required = false) String productName,
        @RequestParam(required = false) String status, // Changed from StockMutationStatus to String
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtStart,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAtEnd,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAtStart,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime updatedAtEnd,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDirection,
        Pageable pageable
) {
    var claims = Claims.getClaimsFromJwt();
    var email = (String) claims.get("sub");
    Page<StockMutationResponseDto> response = stockMutationService.getAllStockMutations(
            email, originWarehouseId, destinationWarehouseId, productName, status,
            createdAtStart, createdAtEnd, updatedAtStart, updatedAtEnd,
            sortBy, sortDirection, pageable
    );


    return Response.successfulResponse("Stock Mutations retrieved successfully", response);
}


}
