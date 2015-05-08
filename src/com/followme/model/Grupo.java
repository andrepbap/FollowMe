package com.followme.model;

public class Grupo {
	private int id;
	private String nome, descricao, foto_patch;
	private Usuario admin;
 
    public Grupo() {
    }

	public Grupo(int id, String nome, String descricao, String foto_patch, Usuario admin) {
    	this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.foto_patch = foto_patch;
        this.admin = admin;
    }

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getFoto_patch() {
		return foto_patch;
	}

	public void setFoto_patch(String foto_patch) {
		this.foto_patch = foto_patch;
	}

	public Usuario getAdmin() {
		return admin;
	}

	public void setAdmin(Usuario admin) {
		this.admin = admin;
	}
 
	 public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
 
}
