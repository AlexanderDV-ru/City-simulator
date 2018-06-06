package ru.alexanderdv.citysimulator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author AlexanderDV
 * @version 0.0.5a
 */
public class AbstractCitizen
{
	public final Long id;
	public final Long bioMather, bioFather;
	public final DNA dna;
	public final double born;
	public Citizen citizen;

	public AbstractCitizen(long id, long bioMather, long bioFather, DNA dna, double born)
	{
		this.id = id;
		this.bioMather = bioMather;
		this.bioFather = bioFather;
		this.dna = dna;
		this.born = born;
	}

	public AbstractCitizen(Citizen citizen)
	{
		this.id = citizen.id;
		this.bioMather = citizen.bioMather;
		this.bioFather = citizen.bioFather;
		this.dna = citizen.dna;
		this.born = citizen.born;
		this.citizen = citizen;
	}
}
