package com.fooddelivery.user.service;

import com.fooddelivery.user.dto.*;
import com.fooddelivery.user.entity.Address;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.repository.AddressRepo;
import com.fooddelivery.user.repository.UserRepo;
import com.fooddelivery.user.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final AddressRepo addressRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    public UserService(UserRepo userRepo, AddressRepo addressRepo, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.addressRepo = addressRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }
    public String register(RegisterRequest request, UserRole role)
    {
        if(userRepo.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email Already Registered");
        User user=new User();
        user.setName(request.getName());
        user.setRole(role);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        userRepo.save(user);
        return "Registration successful";
    }
    public AuthResponse login(LoginRequest request)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user=userRepo.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException("User Not Found"));
        String token=jwtUtil.generateToken(user.getId(), user.getEmail(),user.getRole().name());
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );
    }
    public Optional<UserResponse> getUserById(Long id)
    {
        return userRepo.findById(id).map(this::mapToUserResponse);
    }
 public UserResponse updateRole(Long id,UserRole role)
 {
     User user=userRepo.findById(id) .orElseThrow(() -> new RuntimeException("User not found"));
     user.setRole(role);
     return mapToUserResponse(userRepo.save(user));
 }



    public AddressResponse addAddress(Long id,AddressRequest request)
 {
     User user = userRepo.findById(id)
             .orElseThrow(() -> new RuntimeException("User not found"));
     Address address=new Address();
     address.setUser(user);
     address.setStreet(request.getStreet());
     address.setCity(request.getCity());
     address.setState(request.getState());
     address.setZipcode(request.getZipcode());
     address.setIsDefault(request.getIsDefault());
     return mapToAddressResponse(addressRepo.save(address));
 }
    public List<AddressResponse> getAddress(Long id)
 {
     return addressRepo.findByUserId(id).stream().map(this::mapToAddressResponse).collect((Collectors.toList()));
 }
    private UserResponse mapToUserResponse(User user) {
        UserResponse response=new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setActive(user.getActive());
        return response;
    }
    private AddressResponse mapToAddressResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setStreet(address.getStreet());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setZipcode(address.getZipcode());
        response.setIsDefault(address.getIsDefault());
        return response;
    }
}
