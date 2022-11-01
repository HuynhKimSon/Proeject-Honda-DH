package vn.com.irtech.irbot.business.mapper;

import java.util.List;
import vn.com.irtech.irbot.business.domain.Psc;

public interface PscMapper {

	public Psc selectPscById(Long id);

	public List<Psc> selectPscList(Psc psc);

	public int insertPsc(Psc psc);

	public int updatePsc(Psc psc);

	public int deletePscById(Long id);

	public int deletePscByIds(String[] ids);
	
	public List<Psc> selectPscByVehicleCode(String vehicleCode);
}