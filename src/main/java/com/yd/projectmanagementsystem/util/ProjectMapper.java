package com.yd.projectmanagementsystem.util;

import com.yd.projectmanagementsystem.dto.ProjectDTO;
import com.yd.projectmanagementsystem.model.Project;

public class ProjectMapper {

    public static ProjectDTO toDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCategory(project.getCategory());
        dto.setTags(project.getTags());

        // Set the owner's ID
        if (project.getOwner() != null) {
            dto.setOwnerId(project.getOwner().getId());
        }

        return dto;
    }
}