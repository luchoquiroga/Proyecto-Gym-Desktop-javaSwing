package com.gym.app.services;

import com.google.gson.reflect.TypeToken;
import com.gym.app.models.Plan;

import java.lang.reflect.Type;
import java.util.List;

public class PlanService {

    public List<Plan> listarPlanes() throws Exception {
        Type tipoLista = new TypeToken<List<Plan>>(){}.getType();
        return ApiClient.getList("/planes", tipoLista);
    }

    public Plan crearPlan(Plan nuevoPlan) throws Exception {
        return ApiClient.post("/planes", nuevoPlan, Plan.class);
    }
}