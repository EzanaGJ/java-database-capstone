package com.project.back_end.mvc;

@Controller
public class DashboardController {

    @Autowired
    private Service service;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        if (service.validateToken(token, "admin").isEmpty()) {
            return "admin/adminDashboard";
        } else {
            return "redirect:http://localhost:8080";
        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        if (service.validateToken(token, "doctor").isEmpty()) {
            return "doctor/doctorDashboard";
        } else {
            return "redirect:http://localhost:8080";
        }
    }
}
