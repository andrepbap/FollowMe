package com.followme.entity;

public class User {

	private int id;
	private String nome;
	private String email;
	private String senha;
	private String url;
	private String autorizado;
	private int logado;

	public User() {
		super();
	}
	
	public User(int id, String nome, String email, String senha, int logado) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.logado = logado;
	}

	public User(int id, String url, String autorizado, String nome, String email, String senha, int logado) {
		super();
		this.id = id;
		this.url = url;
		this.autorizado = autorizado;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.logado = logado;

	}

	public String getAutorizado() {
		return autorizado;
	}

	public void setAutorizado(String autorizado) {
		this.autorizado = autorizado;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public String getSenha() {
		return senha;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public int getLogado() {
		return logado;
	}

	public void setLogado(int logado) {
		this.logado = logado;
	}

	@Override
	public String toString() {
		return "Motorista [id=" + id + ", nome=" + nome + ", email=" + email
				+ "]";
	}

}