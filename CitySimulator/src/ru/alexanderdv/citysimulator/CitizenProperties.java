package ru.alexanderdv.citysimulator;

/**
 * 
 * @author AlexanderDV
 * @version 0.0.5a
 */
public class CitizenProperties
{
	final Gender physicalGender;
	final Gender mentalGender;
	final Orientation orientation;
	final HistocompatibilityProtein[] histocompatibilityProteins;

	public static CitizenProperties genByDNA(DNA dna)
	{
		return new CitizenProperties(dna);
	}

	public CitizenProperties(DNA dna)
	{
		this.physicalGender = (dna.nucl[0] == 0 || dna.nucl[0] == 1) ? Gender.Man : Gender.Woman;
		this.mentalGender = dna.nucl[1] == 0 && dna.nucl[2] == 1 && dna.nucl[3] == 2 && dna.nucl[4] == 3
				&& dna.nucl[5] == 0 && dna.nucl[6] == 1
						? (this.physicalGender == Gender.Woman ? Gender.Man : Gender.Woman)
						: (this.physicalGender == Gender.Man ? Gender.Man : Gender.Woman);
		int i = dna.nucl[7] * 1 + dna.nucl[8] * 4 + dna.nucl[9] * 16 + dna.nucl[10] * 64;

		if (i >= 0 && i <= 24)
			this.orientation = Orientation.HomosexualD;
		else if (i >= 25 && i <= 59)
			this.orientation = Orientation.HomosexualC;
		else if (i >= 60 && i <= 86)
			this.orientation = Orientation.HomosexualB;
		else if (i >= 87 && i <= 108)
			this.orientation = Orientation.HomosexualA;
		else if (i >= 109 && i <= 121)
			this.orientation = Orientation.Bisexual;
		else if (i >= 122 && i <= 147)
			this.orientation = Orientation.HeterosexualA;
		else if (i >= 148 && i <= 180)
			this.orientation = Orientation.HeterosexualB;
		else if (i >= 181 && i <= 221)
			this.orientation = Orientation.HeterosexualC;
		else if (i >= 222 && i <= 252)
			this.orientation = Orientation.HeterosexualD;
		else
			this.orientation = Orientation.Asexual;
		
		
		this.histocompatibilityProteins = new HistocompatibilityProtein[6];
		this.histocompatibilityProteins[0] = HistocompatibilityProtein.values()[dna.nucl[11]];
		this.histocompatibilityProteins[1] = HistocompatibilityProtein.values()[dna.nucl[12]];
		this.histocompatibilityProteins[2] = HistocompatibilityProtein.values()[dna.nucl[13]];
		this.histocompatibilityProteins[3] = HistocompatibilityProtein.values()[dna.nucl[14]];
		this.histocompatibilityProteins[4] = HistocompatibilityProtein.values()[dna.nucl[15]];
		this.histocompatibilityProteins[5] = HistocompatibilityProtein.values()[dna.nucl[16]];
	}

	@Override
	public String toString()
	{
		return "CitizenProperties [physicalGender=" + physicalGender + ", mentalGender=" + mentalGender + "]";
	}

}
