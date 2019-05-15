from .pyjnius_utils import JIndexHelpers, JGenerators

import logging
logger = logging.getLogger(__name__)

class Generator:
        
    def __init__(self, generator_class):
        self.counters = JIndexHelpers.JCounters()
        self.args = JIndexHelpers.JArgs()
        self.generator_class = generator_class
        self.generator = self._get_generator()
    
    def _get_generator(self):
        try:
            return JGenerators[self.generator_class].value(self.args, self.counters)
        except:
            raise ValueError(self.generator_class)
            