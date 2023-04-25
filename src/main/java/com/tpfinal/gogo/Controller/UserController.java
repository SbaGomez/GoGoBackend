package com.tpfinal.gogo.Controller;

import com.tpfinal.gogo.Model.User;
import com.tpfinal.gogo.Service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController
{
    @Autowired
    private UserService us;

    @GetMapping("consultarUser/{id}")
    public ResponseEntity<?> consultarUser(@PathVariable final @NotNull Integer id)
    {
        return ResponseEntity.ok(us.getUser(id));
    }

    @PostMapping
    @RequestMapping(value = "addUser",method = RequestMethod.POST)
    public User addUser (@RequestBody User usr)
    {
        return us.addUser(usr).getBody();
    }

    @PostMapping("/{id}/updateUser")
    public ResponseEntity<User> updateUser(@PathVariable final @NotNull Integer id, @RequestBody final User usr)
    {
        return us.updateUser(id, usr);
    }

    @PostMapping("/{id}/deleteUser")
    public ResponseEntity<?> deleteUser(@PathVariable final @NotNull Integer id)
    {
        return us.deleteUser(id);
    }
}
