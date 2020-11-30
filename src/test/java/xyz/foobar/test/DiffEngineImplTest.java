package xyz.foobar.test;

import org.junit.Test;
import xyz.foobar.Diff;
import xyz.foobar.DiffEngine;
import xyz.foobar.DiffEngineImpl;
import xyz.foobar.DiffException;
import xyz.foobar.DiffRenderer;
import xyz.foobar.DiffRendererImpl;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DiffEngineImplTest {

    @Test
    public void whenOriginalIsNull() throws Exception {
        DiffEngine diffEngine = new DiffEngineImpl();
        DiffRenderer renderer = new DiffRendererImpl();

        Person modifiedPerson = new Person();
        modifiedPerson.setFirstName("Fred");
        modifiedPerson.setSurname("Smith");

        Person friend = new Person();
        friend.setFirstName("Tshepo");
        modifiedPerson.setFriend(friend);

        Pet pet = new Pet();
        pet.setName("Bruno");
        pet.setType("Dog");
        modifiedPerson.setPet(pet);

        Set<String> nickNames = new HashSet<String>();
        nickNames.add("Nick");
        nickNames.add("Kane");
        modifiedPerson.setNickNames(nickNames);

        Diff<Person> diff = diffEngine.calculate(null, modifiedPerson);
        System.out.println(renderer.render(diff));
        assertNotNull(diff);
        assertEquals(diff.getVal(), modifiedPerson);
        System.out.println(diff.getDifferencesList().size());
        assertEquals(diff.getDifferencesList().size(), 12);
    }

    @Test
    public void applicationShouldRevertTheModifedChangesToOriginal() throws Exception {
        DiffEngine diffEngine = new DiffEngineImpl();
        DiffRenderer diffRenderer = new DiffRendererImpl();
        Person modified = new Person();
        modified.setFirstName("Fred");
        modified.setSurname("Smith");

        Diff<Person> diff = diffEngine.calculate(null, modified);
        System.out.println(diffRenderer.render(diff));


        Person newPerson = new Person();
        newPerson.setFirstName("FreddyChanged");
        newPerson.setSurname("SmithChanged");

        // Should revert the changes back to original
        Person applied = diffEngine.apply(newPerson, diff);
        assertNotNull(applied);
        //Reverted to Smith
        assertEquals(applied.getSurname(), "Smith");
        assertEquals(applied.getSurname(), modified.getSurname());
        //Reverted to Fred
        assertEquals(applied.getFirstName(), "Fred");
        assertEquals(applied.getFirstName(), modified.getFirstName());
    }

    @Test
    public void whenModifiedIsNull() throws Exception {
        DiffEngine diffEngine = new DiffEngineImpl();
        DiffRenderer renderer = new DiffRendererImpl();

        Person original = new Person();
        original.setFirstName("Fred");
        original.setSurname("Smith");

        Diff<Person> diff = diffEngine.calculate(original, null);
        System.out.println(renderer.render(diff));

        assertNotNull(diff);
    }

    @Test
    public void whenModifiedAndOriginalAreNotNull() throws Exception {
        DiffEngine diffEngine = new DiffEngineImpl();
        DiffRenderer renderer = new DiffRendererImpl();

        // Original
        Person originalPerson = new Person();
        originalPerson.setFirstName("Fred");
        originalPerson.setSurname("Smith");


        // Modified
        Person modifiedPerson = new Person();
        modifiedPerson.setFirstName("Fred");
        modifiedPerson.setSurname("Johnson");
        Person friend = new Person();
        friend.setFirstName("Tshepo");
        modifiedPerson.setFriend(friend);

        Diff<Person> diff = diffEngine.calculate(originalPerson, modifiedPerson);
        System.out.println(renderer.render(diff));

        Person applied = diffEngine.apply(modifiedPerson, diff);
        assertEquals("Smith", originalPerson.getSurname());

    }

    @Test
    public void checkDiffListNotEmpty() throws Exception {
        DiffEngine diffEngine = new DiffEngineImpl();

        Person modifiedPerson = new Person();
        modifiedPerson.setFirstName("Fred");
        modifiedPerson.setSurname("Smith");

        Person friend = new Person();
        friend.setFirstName("Tshepo");
        modifiedPerson.setFriend(friend);

        Pet pet = new Pet();
        pet.setName("Bruno");
        pet.setType("Dog");
        modifiedPerson.setPet(pet);

        Set<String> nickNames = new HashSet<String>();
        nickNames.add("Nick");
        nickNames.add("Kane");
        modifiedPerson.setNickNames(nickNames);

        Diff<Person> diff = diffEngine.calculate(null, modifiedPerson);

        assertNotNull(diff.getVal());
        assertNotNull(diff.getDifferencesList());
        assertEquals(diff.getDifferencesList().size(), 12);
    }

    @Test(expected = DiffException.class)
    public void inputObjectsAreNotSame() throws Exception {
        DiffEngine diffEngine = new DiffEngineImpl();

        Person modifiedPerson = new Person();
        modifiedPerson.setFirstName("Fred");
        modifiedPerson.setSurname("Smith");

        Pet pet = new Pet();
        pet.setName("Bruno");
        pet.setType("Dog");

        Diff<Person> diff = diffEngine.calculate(modifiedPerson, new DupPerson());
    }

}
