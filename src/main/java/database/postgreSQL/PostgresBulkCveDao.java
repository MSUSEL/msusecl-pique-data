package database.postgreSQL;

import businessObjects.cveData.Cve;
import database.IBulkDao;

import java.util.List;

public class PostgresBulkCveDao implements IBulkDao<List<Cve>> {}
