package com.selimhorri.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.helper.UserMappingHelper;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public List<UserDto> findAll() {
		log.info("*** UserDto List, service; fetch all users *");
		return this.userRepository.findAll()
				.stream()
				.map(UserMappingHelper::map)
				.distinct()
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public UserDto findById(final Integer userId) {
		log.info("*** UserDto, service; fetch user by id *");
		return this.userRepository.findById(userId)
				.map(UserMappingHelper::map)
				.orElseThrow(
						() -> new UserObjectNotFoundException(String.format("User with id: %d not found", userId)));
	}

	@Override
	public UserDto save(final UserDto userDto) {
		log.info("*** UserDto, service; save user *");
		userDto.setUserId(null); // o 0, dependiendo de tu tipo y lógica

		return UserMappingHelper.map(this.userRepository.save(UserMappingHelper.map(userDto)));
	}

	@Override
	public UserDto update(final UserDto userDto) {
		log.info("*** UserDto, service; update user ***");

		// Buscar la entidad existente
		User existingUser = this.userRepository.findById(userDto.getUserId())
				.orElseThrow(() -> new EntityNotFoundException("User not found"));

		// Actualizar los campos
		existingUser.setFirstName(userDto.getFirstName());
		existingUser.setLastName(userDto.getLastName());
		existingUser.setImageUrl(userDto.getImageUrl());
		existingUser.setEmail(userDto.getEmail());
		existingUser.setPhone(userDto.getPhone());

		// Actualizar credential si existe
		if (userDto.getCredentialDto() != null && existingUser.getCredential() != null) {
			Credential existingCredential = existingUser.getCredential();
			existingCredential.setUsername(userDto.getCredentialDto().getUsername());
			existingCredential.setPassword(userDto.getCredentialDto().getPassword());
			existingCredential.setRoleBasedAuthority(userDto.getCredentialDto().getRoleBasedAuthority());
			existingCredential.setIsEnabled(userDto.getCredentialDto().getIsEnabled());
			existingCredential.setIsAccountNonExpired(userDto.getCredentialDto().getIsAccountNonExpired());
			existingCredential.setIsAccountNonLocked(userDto.getCredentialDto().getIsAccountNonLocked());
			existingCredential.setIsCredentialsNonExpired(userDto.getCredentialDto().getIsCredentialsNonExpired());
		}

		return UserMappingHelper.map(this.userRepository.save(existingUser));
	}

	@Override
	public UserDto update(final Integer userId, final UserDto userDto) {
		log.info("*** UserDto, service; update user with userId ***");

		// Buscar la entidad existente por el userId del parámetro
		User existingUser = this.userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

		// Actualizar los campos con los datos del userDto
		existingUser.setFirstName(userDto.getFirstName());
		existingUser.setLastName(userDto.getLastName());
		existingUser.setImageUrl(userDto.getImageUrl());
		existingUser.setEmail(userDto.getEmail());
		existingUser.setPhone(userDto.getPhone());

		// Actualizar credential si existe en el DTO y en la entidad
		if (userDto.getCredentialDto() != null && existingUser.getCredential() != null) {
			Credential existingCredential = existingUser.getCredential();
			existingCredential.setUsername(userDto.getCredentialDto().getUsername());
			existingCredential.setPassword(userDto.getCredentialDto().getPassword());
			existingCredential.setRoleBasedAuthority(userDto.getCredentialDto().getRoleBasedAuthority());
			existingCredential.setIsEnabled(userDto.getCredentialDto().getIsEnabled());
			existingCredential.setIsAccountNonExpired(userDto.getCredentialDto().getIsAccountNonExpired());
			existingCredential.setIsAccountNonLocked(userDto.getCredentialDto().getIsAccountNonLocked());
			existingCredential.setIsCredentialsNonExpired(userDto.getCredentialDto().getIsCredentialsNonExpired());
		}

		return UserMappingHelper.map(this.userRepository.save(existingUser));
	}

	@Override
	public void deleteById(final Integer userId) {
		log.info("*** Void, service; delete user by id *");
		this.userRepository.deleteById(userId);
	}

	@Override
	public UserDto findByUsername(final String username) {
		log.info("*** UserDto, service; fetch user with username *");
		return UserMappingHelper.map(this.userRepository.findByCredentialUsername(username)
				.orElseThrow(() -> new UserObjectNotFoundException(
						String.format("User with username: %s not found", username))));
	}

}
