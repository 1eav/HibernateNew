import enumeration.CourseType;
import models.Course;
import models.Student;
import models.Teacher;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String name = "New Course";
        String description = "description";
        int duration = 55;
        int price = 150_000;
        Teacher newTeacher = new Teacher();
        newTeacher.setName("Roman");

        Course newCourse = new Course();
        newCourse.setName(name);
        newCourse.setDescription(description);
        newCourse.setDuration(duration);
        newCourse.setType(CourseType.PROGRAMMING);
        newCourse.setPrice(price);
        newCourse.setTeacher(newTeacher);

        session.save(newCourse);

        Teacher newTeacher = new Teacher();
        newTeacher.setName("Roman");
        session.save(newTeacher);
        List<Teacher> from_teacher = session.createQuery("FROM Teacher", Teacher.class).list();
        from_teacher.forEach(teacher -> System.out.println(teacher.getName()));
        int teacherId = 51;
        Teacher teacher = session.get(Teacher.class, teacherId);
        if (teacher != null) {
            session.remove(teacher);
        }

        String courseName = "Java-разработчик";
        Course course = session.createQuery("FROM Course WHERE name = :name", Course.class)
                .setParameter("name", courseName)
                .uniqueResult();
        if (course != null) {
            System.out.println("Курс: " + courseName);
            System.out.println("Преподователь: " + course.getTeacher().getName());
            System.out.println("Студенты:");
            for (Student student : course.getStudents()) {
                System.out.println(" ---Имя: " + student.getName() + ", возраст: " + student.getAge());
            }
            System.out.println("Общая стоимость: " + calculateTotalCost(course));
        } else {
            System.out.println("Курс не найден");
        }
        transaction.commit();
        session.close();
        sessionFactory.close();
    }
    private static double calculateTotalCost(Course course) {
        double totalCost = 0.0;

        for (Student student : course.getStudents()) {
            for (Course studentCourse : student.getCourses()) {
                totalCost += studentCourse.getPrice();
            }
        }
        return totalCost;
    }
}