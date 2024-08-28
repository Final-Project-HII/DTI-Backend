package com.hii.finalProject.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@ToString
public class Response<T> {
    private int statusCode;
    private String message;
    boolean success = false;
    private T data;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int totalPages;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long totalData;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer currentPage;

    public Response(int statCode, String statusDesc) {
        statusCode = statCode;
        message = statusDesc;
        if (statusCode == HttpStatus.OK.value()) {
            success = true;
        }

    }

    public static <T> ResponseEntity<Response<Object>> failedResponse(String message) {
        return failedResponse(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    public static <T> ResponseEntity<Response<T>> failedResponse(T data) {
        return failedResponse(HttpStatus.BAD_REQUEST.value(), "Bad request", data);
    }

    public static <T> ResponseEntity<Response<T>> failedResponse(int statusCode, String message) {
        return failedResponse(statusCode, message, null);
    }

    public static <T> ResponseEntity<Response<T>> failedResponse(int statusCode, String message, T data) {
        Response<T> response = new Response<>(statusCode, message);
        response.setSuccess(false);
        response.setData(data);

        return ResponseEntity.status(statusCode).body(response);
    }

    public static <T> ResponseEntity<Response<T>> successfulResponse(String message, T data) {
        return successfulResponse(HttpStatus.OK.value(), message, data);
    }

    public static <T> ResponseEntity<Response<T>> successfulResponse(String message) {
        return successfulResponse(HttpStatus.OK.value(), message, null);
    }

    public static <T> ResponseEntity<Response<T>> successfulResponse(int statusCode, String message, T data) {
        Response<T> response = new Response<>(statusCode, message);
        response.setSuccess(true);
        response.setData(data);
        return ResponseEntity.status(statusCode).body(response);
    }
    public static <T> ResponseEntity<Response<T>> successfulResponseWithPage(int statusCode, String message, T data,int totalPages, long totalData,int currentPage) {
        Response<T> response = new Response<>(statusCode, message);
        response.setSuccess(true);
        response.setCurrentPage(currentPage);
        response.setData(data);
        response.setTotalData(totalData);
        response.setTotalPages(totalPages);
        return ResponseEntity.status(statusCode).body(response);
    }
}
