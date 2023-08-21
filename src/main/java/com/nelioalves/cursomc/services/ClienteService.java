package com.nelioalves.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.nelioalves.cursomc.domain.Categoria;
import com.nelioalves.cursomc.domain.Cidade;
import com.nelioalves.cursomc.domain.Cliente;
import com.nelioalves.cursomc.domain.Endereco;
import com.nelioalves.cursomc.domain.enums.TipoCliente;
import com.nelioalves.cursomc.dto.ClienteDTO;
import com.nelioalves.cursomc.dto.ClienteNewDTO;
import com.nelioalves.cursomc.repositories.ClienteRepository;
import com.nelioalves.cursomc.repositories.EnderecoRepository;
import com.nelioalves.cursomc.services.exceptions.DataIntegrityException;
import com.nelioalves.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;

	@Autowired
	private EnderecoRepository enderecoRepository;

	public Cliente find(Integer id) {
		Optional<Cliente> obj = repo.findById(id);
		
		return  obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}

	public Cliente update(Cliente obj) {
		Cliente newObj =  find(obj.getId());
		updateData(newObj,obj);
		return repo.save(newObj);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		}catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possivel excluir porque há pedidos relacionadas.");
		}
		
		
	}

	public List<Cliente> findAll() {
	
		return repo.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String dicetion ){
		
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(dicetion), orderBy );
		return repo.findAll(pageRequest);
		
		
	}
	
	public Cliente fromDTO(ClienteDTO objDTO) {
		
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null);

	}

	public Cliente fromDTO(ClienteNewDTO objDTO) { 
		Cliente clie = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOucnpj(), TipoCliente.toEnum(objDTO.getTipo()));
		Cidade city = new Cidade(objDTO.getCidadeId(), null, null);
		Endereco end = new Endereco(null, 
					objDTO.getLogadouro(), 
					objDTO.getNumero(),
					objDTO.getComplemento(),
					objDTO.getBairro(),
					objDTO.getCep(), clie, city);

		clie.getEnderecos().add(end);
		clie.getTelefones().add(objDTO.getTelefone1());
		if (objDTO.getTelefone2() != null) {
			clie.getTelefones().add(objDTO.getTelefone2());
		}

		if (objDTO.getTelefone3() != null) {
			clie.getTelefones().add(objDTO.getTelefone3());
		}
		
		return clie;
	}

	private void updateData(Cliente newObj, Cliente obj) {
	
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
		// newObj.setNome(obj.getNome());
		// newObj.setNome(obj.getNome());
	}
}
