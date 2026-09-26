package com.oop.carrental.controller;

import com.oop.carrental.dto.BranchDTO;
import com.oop.carrental.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService service;

    public BranchController(BranchService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BranchDTO>> getAll() {

        List<BranchDTO> branchList = service.findAll();
        return ResponseEntity.ok(branchList);

    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getById(@PathVariable Long id) {

        BranchDTO branch = service.findById(id);
        return ResponseEntity.ok(branch);

    }

    @PostMapping
    public ResponseEntity<BranchDTO> create(@Valid @RequestBody BranchDTO dto) {

        BranchDTO savedBranch = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBranch);

    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchDTO> update(@PathVariable Long id, @Valid @RequestBody BranchDTO dto) {

        BranchDTO updatedBranch = service.update(id, dto);
        return ResponseEntity.ok(updatedBranch);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();

    }

}
