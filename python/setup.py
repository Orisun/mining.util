from setuptools import setup, find_packages

setup(
    name="OrisunMining",
    version="1.0.0",
    description='data mining utility of Orisun',
    license='MIT License',
    url='https://github.com/Orisun',

    author='orisun',
    author_email='zhchya@gmail.com',

    packages=find_packages(),
    include_package_data=True,
    platforms='any',
    install_requires=['MySQL-python>=1.2.5', 'jieba>=0.37', 'logging>=0.4.9.6',
                      'APScheduler>=3.0.4', 'psutil>=3.3.0', 'DBUtils>=1.1',
                      'chardet>=2.3.0', 'redis', 'pymongo', 'kafka', 'kazoo',
                      'numpy', 'scipy', 'sklearn', 'pandas', 'threadpool', 'line_profiler', 'requests'],
    scripts=[],
)
