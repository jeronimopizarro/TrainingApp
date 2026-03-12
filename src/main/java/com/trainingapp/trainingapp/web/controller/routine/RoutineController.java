package com.trainingapp.trainingapp.web.controller.routine;

import com.trainingapp.trainingapp.application.usecase.routine.*;
import com.trainingapp.trainingapp.web.dto.routine.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routines")
public class RoutineController {

    private final CreateRoutineUseCase createRoutineUseCase;
    private final GetRoutineByIdUseCase getRoutineByIdUseCase;
    private final GetAllRoutinesByMemberIdUseCase getAllRoutinesByMemberIdUseCase;
    private final ActivateRoutineUseCase activateRoutineUseCase;
    private final GetActiveRoutineUseCase getActiveRoutineUseCase;
    private final InactiveRoutineUseCase inactiveRoutineUseCase;
    private final DuplicateRoutineUseCase duplicateRoutineUseCase;
    private final UpdateRoutineUseCase updateRoutineUseCase;
    private final DeleteRoutineUseCase deleteRoutineUseCase;
    private final CompleteRoutineUseCase completeRoutineUseCase;
    private final GetAllRoutinesByTrainerIdUseCase getAllRoutinesByTrainerIdUseCase;

    public RoutineController(CreateRoutineUseCase createRoutineUseCase,
                             GetRoutineByIdUseCase getRoutineByIdUseCase,
                             GetAllRoutinesByMemberIdUseCase getAllRoutinesByMemberIdUseCase,
                             ActivateRoutineUseCase activateRoutineUseCase,
                             GetActiveRoutineUseCase getActiveRoutineUseCase,
                             InactiveRoutineUseCase inactiveRoutineUseCase,
                             DuplicateRoutineUseCase duplicateRoutineUseCase,
                             UpdateRoutineUseCase updateRoutineUseCase,
                             DeleteRoutineUseCase deleteRoutineUseCase,
                             CompleteRoutineUseCase completeRoutineUseCase,
                             GetAllRoutinesByTrainerIdUseCase getAllRoutinesByTrainerIdUseCase) {
        this.createRoutineUseCase = createRoutineUseCase;
        this.getRoutineByIdUseCase = getRoutineByIdUseCase;
        this.getAllRoutinesByMemberIdUseCase = getAllRoutinesByMemberIdUseCase;
        this.activateRoutineUseCase = activateRoutineUseCase;
        this.getActiveRoutineUseCase = getActiveRoutineUseCase;
        this.inactiveRoutineUseCase = inactiveRoutineUseCase;
        this.duplicateRoutineUseCase = duplicateRoutineUseCase;
        this.updateRoutineUseCase = updateRoutineUseCase;
        this.deleteRoutineUseCase = deleteRoutineUseCase;
        this.completeRoutineUseCase = completeRoutineUseCase;
        this.getAllRoutinesByTrainerIdUseCase = getAllRoutinesByTrainerIdUseCase;
    }

    //TODO: logica de negocio que valide que el member solo puede crear una rutina para el mismo, no para otro member.
    //TODO: logica de negocio que valide que el member solo puede crear una rutina en su gimnasio.
    //TODO: logica de negocio que valide que el trainer solo puede crear una rutina en su gimnasio.
    //TODO: logica de negocio que valide que el trainer solo puede crear una rutina a un miembro de su gimnasio.
    //TODO: logica de negocio que valide que el gym admin solo puede crear una rutina en su gimnasio.
    //TODO: logica de negocio que valide que el gym admin solo puede crear una rutina a un miembro de su gimnasio.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @PostMapping
    public ResponseEntity<CreateRoutineResponse> createRoutine(
            @Valid @RequestBody CreateRoutineRequest routineRequest) {
        CreateRoutineResponse response = createRoutineUseCase.execute(routineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @PostMapping("/{routineId}/duplicate")
    public ResponseEntity<CreateRoutineResponse> duplicateRoutine(@PathVariable Long routineId,
                                                                  @Valid @RequestBody DuplicateRoutineRequest duplicateRoutineRequest) {
        CreateRoutineResponse response = duplicateRoutineUseCase.execute(routineId,
                duplicateRoutineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //TODO: logica de negocio que valide que el gym admin solo obtener una rutina de su gimnasio.
    //TODO: logica de negocio que valide que el trainer solo puede obtener una rutina de su gimnasio.
    //TODO: logica de negocio que valide que el trainer solo puede obtener una rutina que el creó.
    //TODO: logica de negocio que valide que el member solo puede obtener una rutina que el creó.
    //TODO: logica de negocio que valide que el member solo puede obtener una rutina de su gimnasio.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<RoutineDetailResponse> getRoutine(@PathVariable Long id) {
        RoutineDetailResponse response = getRoutineByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    //TODO: logica de negocio que valide que el member solo puede obtener todas sus rutinas.
    //TODO: logica de negocio que valide que el member solo puede obtener todas sus rutinas del gimnasio al que asiste (redundate)
    //TODO: logica de negocio que valide que el trainer solo puede obtener todas sus rutinas.
    //TODO: logica de negocio que valide que el trainer solo puede obtener todas sus rutinas del gimnasio al que asiste (redundate)
    //TODO: logica de negocio que valide que el gym admin solo puede obtener todas sus rutinas.
    //TODO: logica de negocio que valide que el gym admin solo puede obtener todas sus rutinas del gimnasio al que asiste (redundate)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping()
    public ResponseEntity<List<GetAllRoutinesByMemberIdResponse>> getAllRoutinesByMember(
            @RequestParam Long memberId) {
        List<GetAllRoutinesByMemberIdResponse> response = getAllRoutinesByMemberIdUseCase.execute(
                memberId);
        return ResponseEntity.ok(response);
    }

    //TODO: logica de negocio que valide que el trainer solo puede obtener todas sus rutinas. No la de un compañero de trabajo.
    //TODO: logica de negocio que valide que el gym admin solo puede obtener todas las rutinas de sus trainers de su gimnasio.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @GetMapping(params = "trainerId")
    public ResponseEntity<List<GetAllRoutinesByTrainerIdResponse>> getAllRoutinesByTrainer(
            @RequestParam Long trainerId) {

        List<GetAllRoutinesByTrainerIdResponse> response =
                getAllRoutinesByTrainerIdUseCase.execute(trainerId);
        return ResponseEntity.ok(response);
    }

    //TODO: logica de negocio que valide que el member solo puede obtener su propia rutina activa.
    //TODO: logica de negocio que valide que el trainer solo puede obtener todas de los miembros de su gym donde trabaja.
    //TODO: logica de negocio que valide que el gym admin solo puede obtener todas de los miembros de su propio gym.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/active")
    public ResponseEntity<RoutineResponse> getActiveRoutine(@RequestParam Long memberId) {
        RoutineResponse response = getActiveRoutineUseCase.execute(memberId);
        return ResponseEntity.ok(response);
    }

    //TODO: logica de negocio que valide que el member solo puede activar su propia rutina y de su mismo gym.
    //TODO: logica de negocio que valide que el trainer solo puede activar una rutina que el creo y de su mismo gym.
    //TODO: logica de negocio que valide que el gym admin solo puede activar una rutina que el creo y de su mismo gym.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateRoutine(@PathVariable Long id,
                                                @Valid @RequestBody
                                                ActivateRoutineRequest request) {
        activateRoutineUseCase.execute(id, request);
        return ResponseEntity.ok().build();
    }

    //TODO: logica de negocio que valide que el member solo puede inactivar su propia rutina y de su mismo gym.
    //TODO: logica de negocio que valide que el trainer solo puede inactivar una rutina que el creo y de su mismo gym.
    //TODO: logica de negocio que valide que el gym admin solo puede inactivar una rutina que el creo y de su mismo gym.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @PatchMapping("/{id}/inactive")
    public ResponseEntity<Void> inactiveRoutine(@PathVariable Long id) {
        inactiveRoutineUseCase.execute(id);
        return ResponseEntity.ok().build();
    }

    //TODO: logica de negocio que valide que el member solo puede completar su propia rutina y de su mismo gym.
    //TODO: logica de negocio que valide que el trainer solo puede completar una rutina que el creo y de su mismo gym.
    //TODO: logica de negocio que valide que el gym admin solo puede completar una rutina de su mismo gym.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> completeRoutine(@PathVariable Long id) {
        completeRoutineUseCase.execute(id);
        return ResponseEntity.ok().build();
    }

    //TODO: logica de negocio que valide que el member solo puede modificar su propia rutina y de su mismo gym.
    //TODO: logica de negocio que valide que el trainer solo puede modificar una rutina que el creo y de su mismo gym.
    //TODO: logica de negocio que valide que el gym admin solo puede modificar una rutina de su mismo gym.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @PutMapping("/{id}")
    public ResponseEntity<CreateRoutineResponse> updateRoutine(@PathVariable Long id,
                                                               @Valid @RequestBody UpdateRoutineRequest request) {
        CreateRoutineResponse response = updateRoutineUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    //TODO: logica de negocio que valide que el member solo puede eliminar su propia rutina y de su mismo gym.
    //TODO: logica de negocio que valide que el trainer solo puede eliminar una rutina que el creo y de su mismo gym.
    //TODO: logica de negocio que valide que el gym admin solo puede eliminar una rutina de su mismo gym.
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id) {
        deleteRoutineUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}