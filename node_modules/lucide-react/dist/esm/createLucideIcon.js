/**
 * @license lucide-react v0.577.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */

import { forwardRef, createElement } from 'react';
import { mergeClasses } from './shared/src/utils/mergeClasses.js';
import { toKebabCase } from './shared/src/utils/toKebabCase.js';
import { toPascalCase } from './shared/src/utils/toPascalCase.js';
import Icon from './Icon.js';

const createLucideIcon = (iconName, iconNode) => {
  const Component = forwardRef(
    ({ className, ...props }, ref) => createElement(Icon, {
      ref,
      iconNode,
      className: mergeClasses(
        `lucide-${toKebabCase(toPascalCase(iconName))}`,
        `lucide-${iconName}`,
        className
      ),
      ...props
    })
  );
  Component.displayName = toPascalCase(iconName);
  return Component;
};

export { createLucideIcon as default };
//# sourceMappingURL=createLucideIcon.js.map
