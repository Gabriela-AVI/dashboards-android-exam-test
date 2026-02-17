package com.afundacion.fp.coruna.dashboards.dialogs;

/*
 * Esta interfaz define un contrato de comunicación
 * entre el diálogo (NewQuestionDialog) y la Activity
 * que lo utiliza (por ejemplo DashboardQuestionsActivity).
 *
 * Sirve para avisar a la Activity cuando:
 * 1. Empieza la petición de crear pregunta
 * 2. Termina la petición (tanto si fue éxito como error)
 *
 * Así el diálogo NO controla directamente la interfaz
 * de la Activity (como el ProgressBar).
 * Solo notifica eventos.
 */
public interface NewQuestionDialogListener {

    /*
     * Se llama justo antes de enviar la petición POST.
     * La Activity lo usa normalmente para:
     * - Mostrar el ProgressBar
     * - Desactivar botones
     */
    void onCreateQuestionRequestHasBeenSent();

    /*
     * Se llama cuando la petición termina
     * (ya sea éxito o error).
     *
     * La Activity suele usarlo para:
     * - Ocultar el ProgressBar
     * - Recargar la lista de preguntas
     */
    void onCreateQuestionRequestHasFinished();
}