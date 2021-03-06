package SelectMapperEvaluator;

import base.TestBase;
import org.datazup.expression.SelectMapperEvaluator;
import org.datazup.pathextractor.PathExtractor;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by ninel on 3/21/16.
 */
public class SelectMapperEvaluatorTest extends TestBase {

    @Test
    public void isPatternMatch(){
        String expression = "SELECT";
        boolean match = false;
        if (Pattern.matches("[a-zA-Z0-9]+", expression)){
            match = true;
        }
        Assert.assertTrue(match);

        match = false;
        expression = "SELECT(";
        if (Pattern.matches("[a-zA-Z0-9]+", expression)){
            match = true;
        }
        Assert.assertTrue(!match);

    }

    @Test
    public void isEvaluatingComplexStringAsString(){

        Map<String,Object> objectMap = new HashMap<String,Object>();
        objectMap.put("dateString", "nesto nebitno");

        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();
        PathExtractor pathExtractor = new PathExtractor(objectMap);

        Object stringValue = evaluator.evaluate("'SELECT VALUES(jpa,dsd)'", pathExtractor);
        Assert.assertNotNull(stringValue);

        Assert.assertTrue(stringValue instanceof String);
    }

    @Test
    public void isParsingDateFormatFunction(){
        Map<String,Object> objectMap = new HashMap<String,Object>();
        objectMap.put("dateString", "Wed May 21 00:00:00 EDT 2008");

        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();
        PathExtractor pathExtractor = new PathExtractor(objectMap);

        // NOTE: this is working as there is no - (minus) in these strings
        Object datetimeRes = evaluator.evaluate("STR_TO_DATE_TIMESTAMP($dateString$, 'EEE MMM d H:m:s z Y')", pathExtractor);
        Assert.assertNotNull(datetimeRes);
        Assert.assertTrue(datetimeRes instanceof Long);

    }

    @Test
    public void isParsingNowDateTimestampFunction(){
        Map<String,Object> objectMap = new HashMap<String,Object>();


        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();
        PathExtractor pathExtractor = new PathExtractor(objectMap);

        // NOTE: this is working as there is no - (minus) in these strings
        Object datetimeRes = evaluator.evaluate("NOW()", pathExtractor);
        Assert.assertNotNull(datetimeRes);
        Assert.assertTrue(datetimeRes instanceof Long);

    }

    @Test
    public void isMovingAndRemovingAndPuttingTest() {
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("SIZE_OF(child.list)", pathExtractor);
        Assert.assertNotNull(o);
        Assert.assertTrue(o instanceof Integer);
        Assert.assertTrue(((Integer)o)==4);

        o = evaluator.evaluate("REMOVE(child.list[last])", pathExtractor);
        Assert.assertNotNull(o);

        o = evaluator.evaluate("SIZE_OF(child.list)==3", pathExtractor);
        Assert.assertTrue(o instanceof Boolean);

        Boolean b =(Boolean)o;
        Assert.assertTrue(b);
    }

    @Test
    public void isSelectingSimpleItemsTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("SELECT(1,2,3)", pathExtractor);
        Assert.assertNotNull(o);
    }


    @Test
    public void isSelectingItemsFromMapTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("SELECT($child.list$, $list$)", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof Map);

        Map<String,Object> objectMap = (Map)o;
        Assert.assertNotNull(objectMap.get("childList"));
        Assert.assertNotNull(objectMap.get("list"));
    }

    @Test
    public void isSelectingItemsFromMapWhereListLastTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("SELECT($child.list[last]$, $list$)", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof Map);

        Map<String,Object> objectMap = (Map)o;
        Assert.assertNotNull(objectMap.get("childList[last]"));
        Assert.assertNotNull(objectMap.get("list"));
    }

    @Test
    public void isSelectingItemsFromMapWhereListIndex0Test(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("SELECT($child.list[0]$, $list$)", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof Map);

        Map<String,Object> objectMap = (Map)o;
        Assert.assertNotNull(objectMap.get("childList[0]"));
        Assert.assertNotNull(objectMap.get("list"));
    }

    @Test
    public void isSelectingItemsFromMapWhereListIndex0TestBenchmark(){
        Map<String,Object> data =  getData();

        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = SelectMapperEvaluator.getInstance();

        long start = System.currentTimeMillis();
        int num = 1000;
        for (int i=0;i<num;i++) {
            evaluator.evaluate("SELECT($child.list[0]$, $list$)", pathExtractor);
            //   Assert.assertNotNull(compiled);
        }
        long end = System.currentTimeMillis();
        System.out.println("Num: "+num+" executed in: "+(end-start)+" ms, average: "+((end-start)/num)+" ms");

    }




    @Test
    public void isUnionListItemsFromMapTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("UNION($child.list$, $list$)", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof List);

        List l = (List)o;
        Assert.assertTrue(l.size()==5);
    }

    @Test
    public void isUnioMapItemsFromMapTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("UNION($child$, $list$)", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof List);

        List l = (List)o;
        Assert.assertTrue(l.size()==2);
    }

    @Test
    public void isKeysItemsFromMapTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("KEYS($child$)", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof List);

        List l = (List)o;
        Assert.assertTrue(l.size()==3);

    }

    @Test
    public void isValuesItemsFromMapTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("VALUES($child$)", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof List);

        List l = (List)o;
        Assert.assertTrue(l.size()==3);

    }

    @Test
    public void isUnionKeyValuesItemsFromMapTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("UNION(KEYS($child$), VALUES($child$))", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof List);

        List l = (List)o;
        Assert.assertTrue(l.size()==6);

    }

    @Test
    public void isToMapTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("MAP(FIELD('firstChildListItem', $child.list[0]$), FIELD('list', $list$))", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof Map);

        Map<String,Object> objectMap = (Map)o;
        Assert.assertNotNull(objectMap.get("firstChildListItem"));
        Assert.assertNotNull(objectMap.get("list"));
    }

    @Test
    public void isToListTest(){
        Map<String,Object> data =  getData();
        PathExtractor pathExtractor = new PathExtractor(data);
        SelectMapperEvaluator evaluator = new SelectMapperEvaluator();

        Object o = evaluator.evaluate("LIST(FIELD('firstChildListItem', $child.list[0]$), FIELD('list', $list$))", pathExtractor);
        Assert.assertNotNull(o);

        Assert.assertTrue(o instanceof List);

        List<Object> objectList = (List)o;

        Assert.assertTrue(objectList.size()==2);
        Assert.assertNotNull(objectList.get(0));
        Assert.assertNotNull(objectList.get(1));

        Object firstObject = objectList.get(0);
        Assert.assertTrue(firstObject instanceof Map);

        Object secondObject = objectList.get(1);
        Assert.assertTrue(secondObject instanceof Map);

        Assert.assertNotNull(((Map)firstObject).get("firstChildListItem"));
        Assert.assertNotNull(((Map)secondObject).get("list"));


    }
}
