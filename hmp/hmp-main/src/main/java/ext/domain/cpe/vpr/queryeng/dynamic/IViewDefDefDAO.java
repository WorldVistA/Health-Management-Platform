package org.osehra.cpe.vpr.queryeng.dynamic;

import org.osehra.cpe.vpr.pom.IPatientObject;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.repository.Repository;

public interface IViewDefDefDAO extends Repository<IPatientObject, String> {
	// save
	public void save(ViewDefDef obj);
	// simple finder
	public ViewDefDef findByName(String name);
	// Get all available dynamic panels
	public List<ViewDefDef> findAll();
	
	public void delete(ViewDefDef obj);
}
