package com.afundacion.fp.coruna.dashboards.server;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * Esta clase se encarga de gestionar la sesión del usuario.
 *
 * Guarda y recupera el token de sesión utilizando SharedPreferences.
 *
 * Está implementada como Singleton, lo que significa
 * que solo puede existir una instancia en toda la aplicación.
 */
public class AuthManager {

    private static AuthManager instance = null; // Instancia única (patrón Singleton)
    private SharedPreferences preferences; // Objeto que permite guardar datos persistentes
    private static String TOKEN_KEY = "TOKEN"; // Clave con la que se guarda el token

    /*
     * Constructor --> Solo se puede crear desde getInstance().
     */
    private AuthManager(Context context) {

        // Creamos/obtenemos un archivo de preferencias llamado DASHBOARDS_PREFS
        this.preferences = context.getSharedPreferences(
                "DASHBOARDS_PREFS",
                Context.MODE_PRIVATE
        );
    }

    /*
     * METODO --> obtener la única instancia.
     * Si no existe, se crea.
     */
    public static AuthManager getInstance(Context context) {

        if (instance == null) {
            instance = new AuthManager(context);
        }

        return instance;
    }

    /*
     *  METODO --> Guarda el token de sesión en SharedPreferences.
     * Se llama después de un login exitoso.
     */
    public void saveSessionToken(String token) {

        SharedPreferences.Editor editor = preferences.edit();

        // Guardamos el token con la clave TOKEN_KEY
        editor.putString(TOKEN_KEY, token);

        // Confirmamos los cambios
        editor.commit();
    }

    /*
     * Recupera el token guardado -> Devuelve null si no existe
     */
    public String getSessionToken() {
        return preferences.getString(TOKEN_KEY, null);
    }

    /*
     * Indica si el usuario está logueado.
     * Si existe token → true
     * Si no existe → false
     */
    public boolean isLoggedIn() {
        return getSessionToken() != null;
    }

    // 1🔒
    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(TOKEN_KEY);   // Eliminamos el token guardado
        editor.apply();             // Guardamos cambios
    }
}