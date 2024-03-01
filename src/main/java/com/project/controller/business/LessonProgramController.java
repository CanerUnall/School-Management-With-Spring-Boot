package com.project.controller.business;

import com.project.payload.request.business.LessonProgramRequest;
import com.project.payload.response.business.LessonProgramResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.UserResponse;
import com.project.service.business.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;

    @PostMapping("/save") // http://localhost:8080/lessonPrograms/save  + POST  + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonProgramResponse> saveLessonProgram(@RequestBody @Valid
                                                                    LessonProgramRequest lessonProgramRequest) {
        return lessonProgramService.saveLessonProgram(lessonProgramRequest);
    }

    @GetMapping("/getAll") // http://localhost:8080/lessonPrograms/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllLessonProgramByList() {
        return lessonProgramService.getAllLessonProgramByList();
    }

    @GetMapping("/getById/{id}")  // http://localhost:8080/lessonPrograms/getById/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public LessonProgramResponse getLessonProgramById(@PathVariable Long id) {
        return lessonProgramService.getLessonProgramById(id);
    }

    // herhangi bir kullanici atamasi yapilmamis butun dersprogramlari getirecegiz
    @GetMapping("/getAllUnassigned") // http://localhost:8080/lessonPrograms/getAllUnassigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllUnassigned() {
        return lessonProgramService.getAllUnassigned();
    }

    @GetMapping("/getAllAssigned")// http://localhost:8080/lessonPrograms/getAllAssigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllAssigned() {
        return lessonProgramService.getAllAssigned();
    }

    //Not: ODEV : delete -->  /delete/{id}
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<String> deleteById(@PathVariable Long id) {
        return lessonProgramService.deleteById(id);
    }

    //Not: ODEV : getAllWithPage --> /getAllLessonProgramByPage

    @GetMapping("/getAllLessonProgramByPage") // http://localhost:8080/lessonPrograms/getAllLessonProgramByPage
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public ResponseEntity<Page<LessonProgramResponse>> getAllLessonProgramByPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                                                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                                                                 @RequestParam(value = "sort", defaultValue = "lessonName") String sort,
                                                                                 @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        Page<LessonProgramResponse> allLessonPrograms = lessonProgramService.getLessonProgramsByPage(page, size, sort, type);
        return new ResponseEntity<>(allLessonPrograms, HttpStatus.OK);
    }


    // bir Ogretmen kendine ait lessonProgramlari getiriyor
    @GetMapping("/getAllLessonProgramByTeacher") // http://localhost:8080/lessonPrograms/getAllLessonProgramByTeacher
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public Set<LessonProgramResponse> getAllLessonProgramByTeacherUsername(HttpServletRequest httpServletRequest) {
        return lessonProgramService.getAllLessonProgramByUser(httpServletRequest);
    }

    // bir Ogrenci kendine ait lessonProgramlari getiriyor

}

/*
2 adet dto biri request digeri response

kullanicidan her zaman request dto alinir. baska bir sey alamazsin.
requesti alirken her zaman mutlaka validation ile gerekli kontrolleri kullanmalisin.
aldigin request dto servis katmanina kadar gelir ve burada pojoya kesinlikle donusecek.
donusen pojoda senaryoya gore eksik olabilecek fieldler ayrica setlenecek.
daha sonra bunu dbye kaydedebilirsin.
dbden kayit edilen dosya gelince onu da kullaniciya direkt gonderemezsin.
kullaniciya cevap verirken mutlaka ama mutlaka response dto ile cevap vermek zorundasin.
responsede validation kontrollerini yapmana gerek yok cunku bu bilgiler zaten senin dbden geliyor.
bu durumda da dbden gelen pojoyu response dto donusturup onu kullaniciya return edeceksin.
*/