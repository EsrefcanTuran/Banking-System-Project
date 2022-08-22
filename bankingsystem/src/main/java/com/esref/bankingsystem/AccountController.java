package com.esref.bankingsystem;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.esref.bankingsystem.mapper.LogMapper;
import com.esref.bankingsystem.models.Account;
import com.esref.bankingsystem.models.Bank;
import com.esref.bankingsystem.models.Log;
import com.esref.bankingsystem.models.User;
import com.esref.bankingsystem.repositories.IAccountRepository;
import com.esref.bankingsystem.repositories.MyBatisBankRepository;
import com.esref.bankingsystem.repositories.MyBatisUserRepository;
import com.esref.bankingsystem.requests.AccountCreateRequest;
import com.esref.bankingsystem.responses.AccountCreateSuccessResponse;
import com.esref.bankingsystem.requests.DepositRequest;
import com.esref.bankingsystem.requests.TransferRequest;
import com.esref.bankingsystem.requests.UserRegisterRequest;
import com.esref.bankingsystem.responses.AccountCreateInvalidTypeResponse;

@RestController
public class AccountController {
	
	@Autowired
    private IAccountRepository accountRepository;
	
	@Autowired
    private MyBatisUserRepository userRepository;
	
	@Autowired
    private MyBatisBankRepository bankRepository;
	
	@Autowired
    private KafkaTemplate<String, String> producer;
	
	@Autowired
	private LogMapper logMapper;
	
	@PostMapping(path = "/accounts")
    public ResponseEntity<?> create(@RequestBody AccountCreateRequest request) {
		Account createdAccount = this.accountRepository.create(request.getName(), request.getSurname(), request.getEmail(), request.getTc(), request.getType());
		if (createdAccount == null) {
			AccountCreateInvalidTypeResponse resp = new AccountCreateInvalidTypeResponse();
			resp.setMessage("Invalid Account Type: " + request.getType());
			return ResponseEntity.badRequest().body(resp);
		} else {
			AccountCreateSuccessResponse resp = new AccountCreateSuccessResponse();
			resp.setMessage("Account Created");
			resp.setAccountNumber(createdAccount.getAccountNumber());
			return ResponseEntity.ok().body(resp);
		}
    }
	
	@GetMapping(path = "/accounts/{id}")
	public ResponseEntity<?> detail(@PathVariable long id) {
		Account a = this.accountRepository.findByAccountId(id);
		return ResponseEntity.ok().lastModified(a.getLastModified()).body(a);
	}
	
	@PatchMapping(path = "/accounts/{id}")
	public ResponseEntity<?> deposit(@PathVariable long id,@RequestBody DepositRequest request){
		Account a = this.accountRepository.deposit(id, request.getAmount());
		Log l = new Log();
		l.setAccountId(id);
		l.setMessage("deposit amount: " + request.getAmount());
		String logMessage = id + " deposit amount:" + request.getAmount();
    	producer.send("logs", logMessage);
    	logMapper.save(l);
    	return ResponseEntity.ok().body(a);
	}
	
    @PostMapping(path = "/accounts/{id}")
    public ResponseEntity<?> transfer(@PathVariable long id,@RequestBody TransferRequest request) {
        boolean result = this.accountRepository.transfer(request.getAmount(), id, request.getTransferredAccountId());
        if (result) {
        	Log l = new Log();
        	l.setAccountId(id);
        	l.setMessage("transfer amount:" + request.getAmount() + ",transferred_account:" + request.getTransferredAccountId());
        	String logMessage = id + " transfer amount:" + request.getAmount() + ",transferred_account:" + request.getTransferredAccountId();
        	producer.send("logs", logMessage);
        	logMapper.save(l);
        	return ResponseEntity.ok().body("Transferred Successfully");
        }
        return ResponseEntity.badRequest().body("Insufficient Balance");
    }
    
    @CrossOrigin(origins = {"http://localhost"})
	@GetMapping(path = "/accounts/logs/{id}")
	public ResponseEntity<?> transactionLogs(@PathVariable long id) {
		List<Log> arr = this.accountRepository.transactionLogs(id);
		return ResponseEntity.ok().body(arr);
	}
    
    @DeleteMapping("/accounts/{id}")
	public ResponseEntity<?> delete(@PathVariable long id) {
    	Account a = this.accountRepository.findByAccountId(id);
    	this.accountRepository.deleteAccount(a);
    	return ResponseEntity.ok().body("Account Deleted");
	}
    
    @PostMapping(path = "/banks")
    public ResponseEntity<?> createbank(@RequestBody String bankname) {
    	Bank createdBank = new Bank();
    	createdBank.setName(bankname);
		this.bankRepository.createbank(createdBank);
		return ResponseEntity.ok().body("Created Successfully");
    }
    
    @PostMapping(path = "/users")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
    	User registeredUser = new User();
    	registeredUser.setUsername(request.getUsername());
    	registeredUser.setEmail(request.getEmail());
    	registeredUser.setPassword(request.getPassword());
		this.userRepository.register(registeredUser);
		return ResponseEntity.ok().body("Created Successfully");
    }

}
