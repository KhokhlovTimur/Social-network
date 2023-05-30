package ru.itis.services.utils;

import org.springframework.ui.Model;

public interface PagesModelsUtils {
    String getViewNameByUsername(String username, Model model, String token);
}
