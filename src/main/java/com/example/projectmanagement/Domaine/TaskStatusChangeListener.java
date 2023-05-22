package com.example.projectmanagement.Domaine;

import com.example.projectmanagement.Service.ProjectImplServ;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusChangeListener {
@Autowired
    private  ProjectImplServ projectService;

    @PostPersist
    @PostUpdate
    public void onTaskStatusChange(Task task) {
        // Récupérer l'activité parente de la tâche
        Activity activity = task.getActivity();

        // Vérifier si toutes les tâches de l'activité ont le statut "done"
        boolean allTasksDone = activity.getTask().stream()
                .allMatch(t -> t.getStatus().equals("done"));

        if (allTasksDone) {
            // Mettre à jour le statut du projet
            Project project = activity.getProject();
            project.setStatus("done");
            projectService.saveProject(project);
        }
    }
}
