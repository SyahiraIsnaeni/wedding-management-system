package com.example.wedding_management_system.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.wedding_management_system.model.Wedding;
import com.example.wedding_management_system.model.Admin;
import com.example.wedding_management_system.model.Location;
import com.example.wedding_management_system.model.Guest;

@Controller
public class WeddingController{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getUser() {
        return "login";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String login(@ModelAttribute(name = "Admin") Admin admin, Model model) {
        String email = admin.getEmail();
        String password = admin.getPassword();
        try {
            String sql = "SELECT * FROM admin WHERE email = ?";
            Admin asli = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Admin.class), email);
            model.addAttribute("asli", asli);
            String emailAsli = asli.getEmail();
            String passAsli = asli.getPassword();
            if (password.equals(passAsli)) {
                return "redirect:/index";
            }
        } catch (EmptyResultDataAccessException e) {
            // TODO: handle exception
            model.addAttribute("invalidCredentials", true);
        }
        model.addAttribute("invalidCredentials", true);
        return "login";
    }

    @GetMapping("/index")
    public String index(Model model) {
        String sql = "SELECT * FROM weddings WHERE DELETED='n'";
        List<Wedding> weddingList = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Wedding.class));
        model.addAttribute("weddings", weddingList);
        return "index";
    }

    @GetMapping("/recycle")
    public String recycle(Model model) {
        String sql = "SELECT * FROM weddings WHERE DELETED='y'";
        List<Wedding> weddingList = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Wedding.class));
        model.addAttribute("weddings", weddingList);
        return "recycle";
    }

    @GetMapping("/admin")
    public String adminList(Model model) {
        String sql = "SELECT * FROM admin";
        List<Admin> adminList = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Admin.class));
        model.addAttribute("admin", adminList);
        return "admin";
    }

    @GetMapping("/location")
    public String locationList(Model model) {
        String sql = "SELECT * FROM location";
        List<Location> locationList = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Location.class));
        model.addAttribute("location", locationList);
        return "location";
    }

    @GetMapping("/guest/{wedding_id}")
    public String guestList(@PathVariable("wedding_id") String wedding_id, Model model) {
        String sql = "SELECT * FROM guests WHERE wedding_id = ?";
        List<Guest> guestList = jdbcTemplate.query(sql,
                BeanPropertyRowMapper.newInstance(Guest.class), wedding_id);
        model.addAttribute("guests", guestList);
        return "guest";
    }
    @GetMapping("/weddingdetail/{wedding_id}")
    public String weddingDetail(@PathVariable("wedding_id") String wedding_id, Model model) {
        String sql = "SELECT * FROM weddings JOIN location ON (weddings.location_id = location.location_id) JOIN admin ON (weddings.admin_id = admin.admin_id) WHERE wedding_id = ?";
        Wedding wedding = jdbcTemplate.queryForObject(sql,
                BeanPropertyRowMapper.newInstance(Wedding.class), wedding_id);
        Location location = jdbcTemplate.queryForObject(sql,
                BeanPropertyRowMapper.newInstance(Location.class), wedding_id);
        Admin admin = jdbcTemplate.queryForObject(sql,
                BeanPropertyRowMapper.newInstance(Admin.class), wedding_id);
        model.addAttribute("weddings", wedding);
        model.addAttribute("location", location);
        model.addAttribute("admin", admin);
        return "weddingdetail";
    }

    @GetMapping("/addguest")
    public String addGuest(Model model) {
        return "addguest";
    }

    @RequestMapping(value ="/addguest")
    public String addGuest(Guest guest, Model model) {

        String sql = "INSERT INTO guests VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                guest.getGuest_id(), guest.getName(),
                guest.getWedding_id());
        return "redirect:/index";
    }

    @GetMapping("/addwedding")
    public String addWedding(Model model) {
        return "addwedding";
    }

    @RequestMapping(value ="/addwedding")
    public String addWedding(Wedding wedding, Model model) {
        String sql = "INSERT INTO weddings VALUES (?, ?, ?, ?, ?, ?, ?, 'n')";
        jdbcTemplate.update(sql,
                wedding.getWedding_id(), wedding.getCouple_name(),
                wedding.getWedding_date(), wedding.getMin_budget(), wedding.getMax_budget(),
                wedding.getLocation_id(), wedding.getAdmin_id());
        return "redirect:/index";
    }

    @GetMapping("/editwedding/{wedding_id}")
    public String editWedding(@PathVariable("wedding_id") String wedding_id, Model model) {
        String sql = "SELECT * FROM weddings WHERE wedding_id = ?";
        Wedding weddings = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Wedding.class), wedding_id);
        model.addAttribute("weddings", weddings);
        return "editwedding";
    }

    @PostMapping("/editwedding")
    public String editWedding(Wedding wedding) {
        String sql = "UPDATE weddings SET wedding_id=?, couple_name = ?, wedding_date = ?, min_budget = ?, max_budget = ?, location_id = ?, admin_id = ? WHERE wedding_id = ?";
        jdbcTemplate.update(sql, wedding.getWedding_id(), wedding.getCouple_name(), wedding.getWedding_date(),
                wedding.getMin_budget(), wedding.getMax_budget(), wedding.getLocation_id(),
                wedding.getAdmin_id(), wedding.getWedding_id());
        return "redirect:/index";
    }

    @GetMapping("/harddeletewedding/{wedding_id}")
    public String hardDelete(@PathVariable("wedding_id") String wedding_id) {
        String sql = "DELETE FROM weddings WHERE wedding_id = ?";
        jdbcTemplate.update(sql, wedding_id);
        return "redirect:/index";
    }

    @GetMapping("/deleteguest/{guest_id}")
    public String deleteGuest(@PathVariable("guest_id") String guest_id) {
        String sql = "DELETE FROM guests WHERE guest_id = ?";
        jdbcTemplate.update(sql, guest_id);
        return "redirect:/index";
    }

    @GetMapping("/softdeletewedding/{wedding_id}")
    public String softDelete(@PathVariable("wedding_id") String wedding_id) {
        String sql = "UPDATE weddings SET DELETED = 'y' WHERE wedding_id = ?";
        jdbcTemplate.update(sql, wedding_id);
        return "redirect:/index";
    }

    @GetMapping("/restore/{wedding_id}")
    public String restore(@PathVariable("wedding_id") String wedding_id) {
        String sql = "UPDATE weddings SET DELETED = 'n' WHERE wedding_id = ?";
        jdbcTemplate.update(sql, wedding_id);
        return "redirect:/index";
    }

    @GetMapping("/search")
    public String hasilsearch(@PathParam("couple_name") String couple_name, Model model) {
        String sql = "SELECT * FROM weddings WHERE LOWER(couple_name) LIKE CONCAT(CONCAT ('%', ?), '%')";
        List<Wedding> wedding = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Wedding.class), couple_name);
        model.addAttribute("weddings", wedding);
        return ("search");
    }

    @GetMapping("/addlocation")
    public String addLocation(Model model) {
        return "addlocation";
    }

    @RequestMapping(value ="/addlocation")
    public String addLocation(Location location, Model model) {
        String sql = "INSERT INTO location VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                location.getLocation_id(), location.getName(), location.getAddress(), location.getCapacity(), location.getPrice());
        return "redirect:/index";
    }

    @GetMapping("/deletelocation/{location_id}")
    public String deleteLocation(@PathVariable("location_id") String location_id) {
        String sql = "DELETE FROM location WHERE location_id = ?";
        jdbcTemplate.update(sql, location_id);
        return "redirect:/index";
    }

    @GetMapping("/editlocation/{location_id}")
    public String editLocation(@PathVariable("location_id") String location_id, Model model) {
        String sql = "SELECT * FROM location WHERE location_id = ?";
        Location location = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Location.class), location_id);
        model.addAttribute("location", location);
        return "editlocation";
    }

    @PostMapping("/editlocation")
    public String editLocation(Location location) {
        String sql = "UPDATE location SET location_id = ?, name = ?, address = ?, capacity = ?, price = ? WHERE location_id = ?";
        jdbcTemplate.update(sql, location.getLocation_id(), location.getName(), location.getAddress(),location.getCapacity(), location.getPrice(), location.getLocation_id());
        return "redirect:/index";
    }

    @GetMapping("/editguest/{guest_id}")
    public String editGuest(@PathVariable("guest_id") String guest_id, Model model) {
        String sql = "SELECT * FROM guests WHERE guest_id = ?";
        Guest guest = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Guest.class), guest_id);
        model.addAttribute("guests", guest);
        return "editguest";
    }

    @PostMapping("/editguest")
    public String editGuest(Guest guest) {
        String sql = "UPDATE guests SET guest_id = ?, name = ? WHERE guest_id = ?";
        jdbcTemplate.update(sql, guest.getGuest_id(), guest.getName(), guest.getGuest_id());
        return "redirect:/index";
    }

}
