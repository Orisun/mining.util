package com.orisun.mining.util.dao.mongo;

import org.junit.Assert;
import org.junit.Test;

public class TestMongoDBName {

	@Test
	public void testValidDBName(){
		String name="5643654/";
		Assert.assertFalse(MongoDBName.validDbName(name));
		name="5643654\\";
		Assert.assertFalse(MongoDBName.validDbName(name));
		name="null";
		Assert.assertFalse(MongoDBName.validDbName(name));
		name="5643654$";
		Assert.assertFalse(MongoDBName.validDbName(name));
		name="5643654.";
		Assert.assertFalse(MongoDBName.validDbName(name));
		name="5643654\"";
		Assert.assertFalse(MongoDBName.validDbName(name));
		name=null;
		Assert.assertFalse(MongoDBName.validDbName(name));
	}
}
