package com.javier.edukka.service;

import java.util.HashMap;
import java.util.Map;

public class HelperClient {

    public static String getBaseURL() {
        return "http://192.168.1.36/edukka/";
    }

    public static String levelCode(String level) {
        String res;
        switch (level) {
            case "Fácil":
            case "Easy":
                res = "easy";
                break;
            case "Normal":
            case "Medium":
                res = "medium";
                break;
            case "Difícil":
            case "Hard":
                res = "hard";
                break;
            default:
                res = "-";
                break;
        }
        return res;
    }

    public static String levelTranslateEs(String level) {
        String res;
        switch (level) {
            case "easy":
                res = "Fácil";
                break;
            case "medium":
                res = "Normal";
                break;
            case "hard":
                res = "Difícil";
                break;
            default:
                res = "-";
                break;
        }
        return res;
    }

    public static String roleTranslateEs(String role) {
        String res;
        if (role.equals("teacher")) {
            res = "Profesor";
        } else {
            res = "Estudiante";
        }
        return res;
    }

    public static String[] array_avatar() {
        return new String[] {"avatar_bird", "avatar_butterfly", "avatar_cat", "avatar_dog", "avatar_elephant", "avatar_fox", "avatar_lion",
                "avatar_panda", "avatar_penguin", "avatar_pig", "avatar_snail", "avatar_snake", "avatar_spider", "avatar_turtle", "avatar_wolf", "avatar_teacher"};
    }

    public static Map<String, String> map_subject() {
        Map<String,String> map = new HashMap<>();
        map.put("Spanish Language", "subject_spanish");
        map.put("Mathematics", "subject_math");
        map.put("Natural Sciences", "subject_natural");
        map.put("Social Sciences", "subject_social");
        map.put("Biology & Geology", "subject_biology");
        map.put("Geography & History", "subject_geography");
        map.put("Music", "subject_music");
        map.put("Sports", "subject_sport");
        map.put("English", "subject_english");
        map.put("General Knowledge", "subject_general");
        return map;
    }

    public static String subjectTranslateEn(String subject) {
        String res;
        switch (subject) {
            case "Lengua Castellana": res = "Spanish Language"; break;
            case "Matemáticas": res = "Mathematics"; break;
            case "Ciencias Naturales": res = "Natural Sciences"; break;
            case "Ciencias Sociales": res = "Social Sciences"; break;
            case "Biología y Geología": res = "Biology & Geology"; break;
            case "Geografía e Historia": res = "Geography & History"; break;
            case "Música": res = "Music"; break;
            case "Deportes": res = "Sports"; break;
            case "Inglés": res = "English"; break;
            case "Conocimiento General": res = "General Knowledge"; break;
            default: res = "-"; break;
        }
        return res;
    }

    public static String subjectTranslateEs(String subject) {
        String res;
        switch (subject) {
            case "Spanish Language": res = "Lengua Castellana"; break;
            case "Mathematics": res = "Matemáticas"; break;
            case "Natural Sciences": res = "Ciencias Naturales"; break;
            case "Social Sciences": res = "Ciencias Sociales"; break;
            case "Biology & Geology": res = "Biología y Geología"; break;
            case "Geography & History": res = "Geografía e Historia"; break;
            case "Music": res = "Música"; break;
            case "Sports": res = "Deportes"; break;
            case "English": res = "Inglés"; break;
            case "General Knowledge": res = "Conocimiento General"; break;
            default: res = "-"; break;
        }
        return res;
    }

    public static String defaultClassNameEs() {
        return "Clase Pública";
    }

    public static String defaultClassInfoEs() {
        return "Clase por defecto para los usuarios que no tienen seleccionada una clase privada";
    }
}
