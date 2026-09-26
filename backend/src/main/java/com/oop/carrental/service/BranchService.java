package com.oop.carrental.service;

import com.oop.carrental.dto.BranchDTO;

import java.util.List;

public interface BranchService {

    List<BranchDTO> findAll();

    BranchDTO findById(Long id);

    BranchDTO save(BranchDTO dto);

    BranchDTO update(Long id, BranchDTO dto);

    void delete(Long id);

}
