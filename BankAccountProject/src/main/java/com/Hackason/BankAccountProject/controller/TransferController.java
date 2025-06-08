package com.Hackason.BankAccountProject.controller;
import com.Hackason.BankAccountProject.param.TransferRequest;
import com.Hackason.BankAccountProject.service.TransferService;
import com.Hackason.BankAccountProject.vo.TransferResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wtt
 * @date 2025/06/08
 */
@RestController
@RequiredArgsConstructor
@Tag(name="工作台")
@RequestMapping("/transfer")
public class TransferController {
    private final TransferService transferService;

    @ApiOperationSupport(order = 1)
    @Operation(summary = "转账")
    @PostMapping("/transfer")
    public TransferResult transfer(TransferRequest request) {
        return transferService.transfer(request);
    }
}
