package com.Hackason.BankAccountProject.service;

import com.Hackason.BankAccountProject.entity.Balance;
import com.Hackason.BankAccountProject.param.TransferRequest;
import com.Hackason.BankAccountProject.vo.TransferResult;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

@Service
public interface TransferService {

    TransferResult transfer(TransferRequest request);
}
