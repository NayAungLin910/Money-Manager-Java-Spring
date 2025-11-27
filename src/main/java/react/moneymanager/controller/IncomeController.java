package react.moneymanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import react.moneymanager.dto.IncomeDTO;
import react.moneymanager.service.IncomeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncomeById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO) {
        IncomeDTO newIncomeDTO = incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newIncomeDTO);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getIncomes() {
        List<IncomeDTO> incomeDTOS = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(incomeDTOS);
    }
}
