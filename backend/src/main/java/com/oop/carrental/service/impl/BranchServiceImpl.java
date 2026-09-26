package com.oop.carrental.service.impl;

import com.oop.carrental.dto.BranchDTO;
import com.oop.carrental.entity.Branch;
import com.oop.carrental.exception.ResourceNotFoundException;
import com.oop.carrental.repository.BranchRepository;
import com.oop.carrental.service.BranchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository repository;

    public BranchServiceImpl(BranchRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BranchDTO> findAll() {

        List<Branch> branchList = repository.findAll();
        List<BranchDTO> dtoList = new ArrayList<>();

        for (Branch branch : branchList) {

            BranchDTO dto = convertToDTO(branch);
            dtoList.add(dto);

        }

        return dtoList;

    }

    @Override
    public BranchDTO findById(Long id) {

        Branch branch = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Branch not found with id: " + id)
        );

        return convertToDTO(branch);

    }

    @Override
    @Transactional
    public BranchDTO save(BranchDTO dto) {

        Branch branch = convertToEntity(dto);
        Branch savedBranch = repository.save(branch);

        return convertToDTO(savedBranch);

    }

    @Override
    @Transactional
    public BranchDTO update(Long id, BranchDTO dto) {

        Branch branch = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Branch not found with id: " + id)
        );

        branch.setName(dto.getName());
        branch.setLocation(dto.getLocation());
        branch.setAddress(dto.getAddress());
        branch.setPhone(dto.getPhone());

        Branch updatedBranch = repository.save(branch);

        return convertToDTO(updatedBranch);

    }

    @Override
    @Transactional
    public void delete(Long id) {

        boolean exists = repository.existsById(id);

        if (!exists) {
            throw new ResourceNotFoundException("Branch not found with id: " + id);
        }

        repository.deleteById(id);

    }

    private BranchDTO convertToDTO(Branch branch) {

        BranchDTO dto = new BranchDTO();

        dto.setId(branch.getId());
        dto.setName(branch.getName());
        dto.setLocation(branch.getLocation());
        dto.setAddress(branch.getAddress());
        dto.setPhone(branch.getPhone());
        dto.setCreatedAt(branch.getCreatedAt());
        dto.setUpdatedAt(branch.getUpdatedAt());

        return dto;

    }

    private Branch convertToEntity(BranchDTO dto) {

        Branch branch = new Branch();

        branch.setName(dto.getName());
        branch.setLocation(dto.getLocation());
        branch.setAddress(dto.getAddress());
        branch.setPhone(dto.getPhone());

        return branch;

    }

}
