package billennium.quizapp;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.join;


@Log4j2
public class ActiveProfileVerifier implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private List<String> availableProfiles = Arrays.asList("dev", "test");

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        String[] activeProfiles = event.getEnvironment().getActiveProfiles();
        if (noProfiles(activeProfiles) || notAcceptedProfile(activeProfiles)) {
            log.error("Spring application active profile is not correct! " +
                            "provided [{}], available [{}]",
                    join(",", activeProfiles),
                    join("/", availableProfiles));
            System.exit(1);
        }
    }

    private boolean noProfiles(String[] activeProfiles) {
        return activeProfiles.length == 0;
    }

    private boolean notAcceptedProfile(String[] activeProfiles) {
        return Stream.of(activeProfiles)
                .noneMatch(profile -> availableProfiles.contains(profile));
    }

}
