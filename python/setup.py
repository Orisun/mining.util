from setuptools import setup, find_packages

setup(
    name="LagouMining",
    version="0.0.1",
    description='data mining utility of Lagou',
    license='MIT License',
    url='http://www.lagou.com/',

    author='orisun',
    author_email='orisun@lagou.com',

    packages=find_packages(),
    include_package_data=True,
    platforms='any',
    install_requires=['MySQL-python>=1.2.5', 'jieba>=0.37', 'logging>=0.4.9.6',
                      'APScheduler>=3.0.4', 'psutil>=3.3.0', 'DBUtils>=1.1',
                      'chardet>=2.3.0', 'redis', 'pymongo', 'kafka', 'kazoo',
                      'numpy', 'scipy', 'sklearn', 'pandas', 'threadpool', 'line_profiler', 'requests'],
    scripts=[],
)
