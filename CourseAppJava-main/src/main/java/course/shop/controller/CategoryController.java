package course.shop.controller;

import course.shop.model.Category;
import course.shop.model.UserDetails;
import course.shop.repositories.CategoryRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;



@Controller
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;








    @GetMapping("/category")
    public String showCategories (Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserId();
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("added", false);
        model.addAttribute("activeLink", "Kategorije");

        return "category";
    }

    @PostMapping("/category/add")
    public String addCategory (@Valid Category category, BindingResult result, Model model,RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) auth.getPrincipal();
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            model.addAttribute("category", category);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("added", true);
            model.addAttribute("activeLink", "Kategorije");
            return "category";
        }
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Kategorija je uspješno dodana!");

        return "redirect:/category";
    }

    @GetMapping("/category/delete/{id}")
    public String deleteCategoy(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

//        Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pogrešan ID"));
//        categoryRepository.delete(category);
//        redirectAttributes.addFlashAttribute("successMessage", "Kategorija je uspješno izbrisana!");

        try {
            categoryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Kategorija je uspješno izbrisana.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Kategorija se ne može izbrisati jer je referencirana u tečajevima.");
        }
        return "redirect:/category";
    }
}
