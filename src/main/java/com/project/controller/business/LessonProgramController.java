package com.project.controller.business;

import com.project.service.business.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lessonPrograms")
public class LessonProgramController {
    private final LessonProgramService lessonProgramService;

}
