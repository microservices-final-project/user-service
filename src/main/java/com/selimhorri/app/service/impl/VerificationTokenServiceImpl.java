package com.selimhorri.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.selimhorri.app.domain.Credential;
import com.selimhorri.app.domain.VerificationToken;
import com.selimhorri.app.dto.VerificationTokenDto;
import com.selimhorri.app.exception.wrapper.CredentialNotFoundException;
import com.selimhorri.app.exception.wrapper.VerificationTokenNotFoundException;
import com.selimhorri.app.helper.VerificationTokenMappingHelper;
import com.selimhorri.app.repository.CredentialRepository;
import com.selimhorri.app.repository.VerificationTokenRepository;
import com.selimhorri.app.service.VerificationTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

	private final VerificationTokenRepository verificationTokenRepository;
	private final CredentialRepository credentialRepository;

	@Override
	public List<VerificationTokenDto> findAll() {
		log.info("*** VerificationTokenDto List, service; fetch all verificationTokens *");
		return this.verificationTokenRepository.findAll()
				.stream()
				.map(VerificationTokenMappingHelper::map)
				.distinct()
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public VerificationTokenDto findById(final Integer verificationTokenId) {
		log.info("*** VerificationTokenDto, service; fetch verificationToken by ids *");
		return this.verificationTokenRepository.findById(verificationTokenId)
				.map(VerificationTokenMappingHelper::map)
				.orElseThrow(() -> new VerificationTokenNotFoundException(String
						.format("#### VerificationToken with id: %d not found! ####", verificationTokenId)));
	}

	@Override
	public VerificationTokenDto save(final VerificationTokenDto verificationTokenDto) {
		log.info("*** VerificationTokenDto, service; save verificationToken *");
		verificationTokenDto.setVerificationTokenId(null);
		// Validar que el ID de la credencial esté presente
		final Integer credentialId = verificationTokenDto.getCredentialDto() != null
				? verificationTokenDto.getCredentialDto().getCredentialId()
				: null;

		if (credentialId == null) {
			throw new IllegalArgumentException("Credential ID must not be null");
		}

		// Verificar que la credencial exista en la base de datos
		final Credential credential = credentialRepository.findById(credentialId)
				.orElseThrow(() -> new CredentialNotFoundException("Credential not found with ID: " + credentialId));

		// Mapear el DTO a entidad
		final VerificationToken verificationToken = VerificationTokenMappingHelper.map(verificationTokenDto);
		verificationToken.setCredential(credential); // Asignar la entidad real

		// Guardar el token
		final VerificationToken savedToken = verificationTokenRepository.save(verificationToken);

		// Retornar el DTO
		return VerificationTokenMappingHelper.map(savedToken);
	}

	@Override
	public VerificationTokenDto update(final VerificationTokenDto verificationTokenDto) {
		log.info("*** VerificationTokenDto, service; update verificationToken *");
		return VerificationTokenMappingHelper.map(this.verificationTokenRepository
				.save(VerificationTokenMappingHelper.map(verificationTokenDto)));
	}

	@Override
	public VerificationTokenDto update(final Integer verificationTokenId,
			final VerificationTokenDto verificationTokenDto) {
		log.info("*** VerificationTokenDto, service; update verificationToken with verificationTokenId *");
		return VerificationTokenMappingHelper.map(this.verificationTokenRepository.save(
				VerificationTokenMappingHelper.map(this.findById(verificationTokenId))));
	}

	@Override
	public void deleteById(final Integer verificationTokenId) {
		log.info("*** Void, service; delete verificationToken by id *");
		this.verificationTokenRepository.deleteById(verificationTokenId);
	}

}
