Feature: Greeting Component

  Scenario: Displaying the correct greeting message
    Given I launch the app
    When I see the Greeting with name "Android"
    Then I should see "Hello Android!" on the screen