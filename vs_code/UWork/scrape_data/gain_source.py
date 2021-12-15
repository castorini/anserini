from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import os
account = "your account"
password = "your password"


def get_driver():
    options = Options()
    options.add_argument('--headless')
    driver = webdriver.Chrome(executable_path=os.path.abspath('chromedriver'), options=options)
    return driver


def connect_to_base(browser):
    base_url = 'https://waterlooworks.uwaterloo.ca/myAccount/co-op/coop-postings.htm'
    try:
        browser.get(base_url)
        elements = browser.find_element_by_class_name("js--btn-search")
        elements.click()
        print(browser.page_source)
    except Exception as ex:
        print(f"Link failed to {base_url}")
        print("Trying to log in")

        elements = browser.find_element_by_class_name("button")
        elements.click()

        elements = browser.find_element_by_class_name("btn--landing")
        elements.click()

        elem_user = browser.find_element_by_name("UserName")
        elem_user.send_keys(account)

        elements = browser.find_element_by_id("nextButton")
        elements.click()

        elem_pwd = browser.find_element_by_name("Password")
        elem_pwd.send_keys(password)

        elements = browser.find_element_by_class_name("submit")
        elements.click()

        browser.get(base_url)
        print(browser.page_source)


website = get_driver()
connect_to_base(website)


