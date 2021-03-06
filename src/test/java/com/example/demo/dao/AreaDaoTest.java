package com.example.demo.dao;

import static org.junit.Assert.*;

import com.example.demo.entity.Area;
import java.util.Date;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AreaDaoTest
{
	@Autowired
	private AreaDao areaDao;

	@Test
	@Ignore
	public void queryArea()
	{
		List<Area> areaList = areaDao.queryArea();
		assertEquals(2, areaList.size());
	}

	@Test
	@Ignore
	public void queryAreaById()
	{
		Area area = areaDao.queryAreaById(1);
		assertEquals("东苑", area.getAreaName());
	}

	@Test
	@Ignore
	public void insertArea()
	{
		Area area = new Area();
		area.setAreaName("南苑");
		area.setPriority(1);
		int effectNum = areaDao.insertArea(area);
		assertEquals(1,effectNum);
	}

	@Test
	@Ignore
	public void updateArea()
	{

		Area area = new Area();
		area.setAreaName("西苑");
		area.setAreaId(3);
		area.setLastEditTime(new Date());

		int effectNum = areaDao.updateArea(area);

		assertEquals(1, effectNum);
	}

	@Test
	public void deleteArea()
	{
		int effectNum = areaDao.deleteArea(3);
		assertEquals(1, effectNum);

	}
}