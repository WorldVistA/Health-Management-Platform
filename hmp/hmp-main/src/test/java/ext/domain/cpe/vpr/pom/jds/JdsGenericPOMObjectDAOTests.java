package org.osehra.cpe.vpr.pom.jds;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.osehra.cpe.jsonc.JsonCCollection;
import org.osehra.cpe.vpr.pom.AbstractPOMObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class JdsGenericPOMObjectDAOTests {

    private JdsGenericPOMObjectDAO dao;

    private JdsOperations mockJdsTemplate;

    @Before
    public void setUp() throws Exception {
        mockJdsTemplate = mock(JdsOperations.class);

        dao = new JdsGenericPOMObjectDAO();
        dao.setJdsTemplate(mockJdsTemplate);
    }

    @Test
    public void testSaveForNewUid() throws Exception {
        when(mockJdsTemplate.postForLocation(eq("/data/foo"), any(HttpEntity.class))).thenReturn(URI.create("/data/urn:va:foo:1234:42"));

        Foo foo = new Foo();
        dao.save(foo);

        verify(mockJdsTemplate).postForLocation("/data/foo", foo);

        assertThat(foo.getUid(), is("urn:va:foo:1234:42"));
    }

    @Test
    public void testSaveWithExistingUid() throws Exception {
        when(mockJdsTemplate.postForLocation(eq("/data"), any(HttpEntity.class))).thenReturn(URI.create("/data/urn:va:foo:1234:42"));

        Foo foo = new Foo();
        foo.setData("uid", "urn:va:foo:1234:42");

        dao.save(foo);

        verify(mockJdsTemplate).postForLocation("/data", foo);

        assertThat(foo.getUid(), is("urn:va:foo:1234:42"));
    }

    @Test
    public void testDeleteByUID() throws Exception {
        dao.deleteByUID(Foo.class, "urn:va:foo:1234:42");

        verify(mockJdsTemplate).delete("/data/urn:va:foo:1234:42");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByUIDNullArg() throws Exception {
        dao.deleteByUID(Foo.class, null);
    }

    @Test
    public void testFindByUID() throws Exception {
        String uid = "urn:va:foo:1234:42";

        Foo mockFoo = new Foo();
        mockFoo.setData("uid", uid);
        when(mockJdsTemplate.getForObject("/data/" + uid, Foo.class)).thenReturn(mockFoo);

        Foo foo = dao.findByUID(Foo.class, uid);
        assertThat(foo, sameInstance(mockFoo));

        verify(mockJdsTemplate).getForObject("/data/" + uid, Foo.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindByUIDNullArg() throws Exception {
        dao.findByUID(Foo.class, null);
    }

    @Test
    public void testFindByUIDNotFound() throws Exception {
        String uid = "urn:va:foo:1234:42";

        when(mockJdsTemplate.getForObject("/data/" + uid, Foo.class)).thenReturn(null);

        Foo foo = dao.findByUID(Foo.class, "urn:va:foo:1234:42");

        assertThat(foo, nullValue());

        verify(mockJdsTemplate).getForObject("/data/" + uid, Foo.class);
    }

    @Test
    public void testFindAll() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();

        List<Map<String, Object>> mockFoos = new ArrayList<Map<String, Object>>();
        mockFoos.add(jsonMapper.convertValue(new Foo("urn:va:foo:1234:42", "foo"), Map.class));
        mockFoos.add(jsonMapper.convertValue(new Foo("urn:va:foo:1234:57", "bar"), Map.class));
        mockFoos.add(jsonMapper.convertValue(new Foo("urn:va:foo:1234:64", "baz"), Map.class));

        when(mockJdsTemplate.getForJsonC("/data/find/foo")).thenReturn(JsonCCollection.create(mockFoos));

        List<Foo> foos = dao.findAll(Foo.class);

        assertThat(foos.size(), is(3));
        verify(mockJdsTemplate).getForJsonC("/data/find/foo");
    }

    @Test
    public void testFindAllWithPagination() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();

        List<Map<String, Object>> mockFoos = new ArrayList<Map<String, Object>>();
        mockFoos.add(jsonMapper.convertValue(new Foo("urn:va:foo:1234:42", "foo"), Map.class));
        mockFoos.add(jsonMapper.convertValue(new Foo("urn:va:foo:1234:57", "bar"), Map.class));
        mockFoos.add(jsonMapper.convertValue(new Foo("urn:va:foo:1234:64", "baz"), Map.class));

        Pageable pageable = new PageRequest(0, 3);
        JsonCCollection<Map<String, Object>> jsonc = JsonCCollection.create(new PageImpl(mockFoos, pageable, 9));

        when(mockJdsTemplate.getForJsonC("/data/find/foo?start=0&limit=3")).thenReturn(jsonc);

        Page<Foo> foos = dao.findAll(Foo.class, pageable);

        assertThat(foos.getNumberOfElements(), is(3));
        assertThat(foos.getTotalElements(), is(9L));
        verify(mockJdsTemplate).getForJsonC("/data/find/foo?start=0&limit=3");
    }

    @Test
    public void testFindAllWithEmptyCollection() throws Exception {
        ObjectMapper jsonMapper = new ObjectMapper();

        List<Map<String, Object>> mockFoos = new ArrayList<Map<String, Object>>();

        when(mockJdsTemplate.getForJsonC("/data/find/foo")).thenReturn(JsonCCollection.create(mockFoos));

        List<Foo> foos = dao.findAll(Foo.class);

        assertThat(foos.size(), is(0));
        verify(mockJdsTemplate).getForJsonC("/data/find/foo");
    }

    // TODO: not sure what this response looks like from the JDS, yet
//    @Test
//    public void testCount() throws Exception {
//        when(mockJdsTemplate.getForJsonC("/data/count/foo")).thenReturn(JsonCCollection.create(Arrays.asList("foo", "bar", "baz")));
//
//        int num = dao.count(Foo.class);
//
//        assertThat(num, is(23));
//        verify(mockJdsTemplate).getForJsonC("/data/count/foo");
//    }

    public static class Foo extends AbstractPOMObject {

        private String bar;

        public Foo() {
            super(null);
        }

        public Foo(Map<String, Object> data) {
            super(data);
        }

        public Foo(String uid) {
            super(null);
            this.setData("uid", uid);
        }

        public Foo(String uid, String bar) {
            super(null);
            this.setData("uid", uid);
            this.setData("bar", bar);
        }

        public String getBar() {
            return bar;
        }
    }
}
